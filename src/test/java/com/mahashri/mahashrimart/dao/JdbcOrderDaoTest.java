package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.model.Order;
import com.mahashri.mahashrimart.model.OrderItem;
import com.mahashri.mahashrimart.model.OrderStatus;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcOrderDaoTest {
    private static final long SELLER_1 = 1L;
    private static final long SELLER_2 = 2L;
    private static final long BUYER_1 = 3L;
    private static final long BUYER_2 = 4L;
    private static final long PRODUCT_A = 101L; // seller 1
    private static final long PRODUCT_B = 201L; // seller 2

    private JdbcDataSource dataSource;
    private JdbcOrderDao orderDao;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:orderdaotest;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            runScript(statement, "db/schema.sql");
            statement.execute("INSERT INTO users (id, name, email, password_hash, role) VALUES "
                    + "(1, 'Seller One', 's1@test.com', 'x', 'SELLER')");
            statement.execute("INSERT INTO users (id, name, email, password_hash, role) VALUES "
                    + "(2, 'Seller Two', 's2@test.com', 'x', 'SELLER')");
            statement.execute("INSERT INTO users (id, name, email, password_hash, role) VALUES "
                    + "(3, 'Buyer One', 'b1@test.com', 'x', 'BUYER')");
            statement.execute("INSERT INTO users (id, name, email, password_hash, role) VALUES "
                    + "(4, 'Buyer Two', 'b2@test.com', 'x', 'BUYER')");
            statement.execute("INSERT INTO products (id, seller_id, name, description, price, stock_qty, category) VALUES "
                    + "(101, 1, 'Wax Crayons', 'desc', 99.00, 10, 'Art Supplies')");
            statement.execute("INSERT INTO products (id, seller_id, name, description, price, stock_qty, category) VALUES "
                    + "(201, 2, 'Notebook', 'desc', 50.00, 10, 'Stationery')");
        }
        orderDao = new JdbcOrderDao(dataSource);
    }

    private static void runScript(Statement statement, String resource) throws Exception {
        try (InputStream input = JdbcOrderDaoTest.class.getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(input, "Missing resource " + resource);
            String script = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            for (String sql : Arrays.stream(script.split(";")).map(String::trim).filter(s -> !s.isBlank()).toList()) {
                statement.execute(sql);
            }
        }
    }

    private long createOrder(long buyerId, OrderStatus status, Object[]... lines) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            Order order = new Order();
            order.setBuyerId(buyerId);
            order.setStatus(status);
            BigDecimal total = BigDecimal.ZERO;
            for (Object[] line : lines) {
                total = total.add(((BigDecimal) line[2]).multiply(BigDecimal.valueOf((int) line[1])));
            }
            order.setTotalAmount(total);
            long orderId = orderDao.create(connection, order);
            for (Object[] line : lines) {
                orderDao.addItem(connection, orderId, (long) line[0], (int) line[1], (BigDecimal) line[2]);
            }
            return orderId;
        }
    }

    private static Object[] line(long productId, int quantity, String price) {
        return new Object[]{productId, quantity, new BigDecimal(price)};
    }

    @Test
    void createdOrderCanBeFoundByIdWithItsItems() throws Exception {
        long orderId = createOrder(BUYER_1, OrderStatus.CONFIRMED, line(PRODUCT_A, 2, "99.00"));

        Optional<Order> found = orderDao.findById(orderId);

        assertTrue(found.isPresent());
        Order order = found.get();
        assertEquals(BUYER_1, order.getBuyerId());
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertEquals(0, new BigDecimal("198.00").compareTo(order.getTotalAmount()));
        assertNotNull(order.getCreatedAt());
        assertEquals(1, order.getItems().size());
        OrderItem item = order.getItems().get(0);
        assertEquals(PRODUCT_A, item.getProductId());
        assertEquals("Wax Crayons", item.getProductName());
        assertEquals(2, item.getQuantity());
    }

    @Test
    void findByIdReturnsEmptyForUnknownOrder() throws Exception {
        assertTrue(orderDao.findById(9999L).isEmpty());
    }

    @Test
    void findByBuyerIdReturnsOnlyThatBuyersOrders() throws Exception {
        createOrder(BUYER_1, OrderStatus.CONFIRMED, line(PRODUCT_A, 1, "99.00"));
        createOrder(BUYER_1, OrderStatus.SHIPPED, line(PRODUCT_B, 1, "50.00"));
        createOrder(BUYER_2, OrderStatus.CONFIRMED, line(PRODUCT_A, 1, "99.00"));

        List<Order> orders = orderDao.findByBuyerId(BUYER_1);

        assertEquals(2, orders.size());
        assertTrue(orders.stream().allMatch(o -> o.getBuyerId() == BUYER_1));
    }

    @Test
    void findAllReturnsEveryOrder() throws Exception {
        createOrder(BUYER_1, OrderStatus.CONFIRMED, line(PRODUCT_A, 1, "99.00"));
        createOrder(BUYER_2, OrderStatus.CONFIRMED, line(PRODUCT_B, 1, "50.00"));

        assertEquals(2, orderDao.findAll().size());
    }

    @Test
    void findBySellerIdOnlyShowsThatSellersItems() throws Exception {
        long mixedOrder = createOrder(BUYER_1, OrderStatus.CONFIRMED,
                line(PRODUCT_A, 1, "99.00"), line(PRODUCT_B, 3, "50.00"));
        createOrder(BUYER_2, OrderStatus.CONFIRMED, line(PRODUCT_B, 1, "50.00"));

        List<Order> sellerOneOrders = orderDao.findBySellerId(SELLER_1);

        assertEquals(1, sellerOneOrders.size());
        Order order = sellerOneOrders.get(0);
        assertEquals(mixedOrder, order.getId());
        assertEquals(1, order.getItems().size());
        assertEquals(PRODUCT_A, order.getItems().get(0).getProductId());

        assertEquals(2, orderDao.findBySellerId(SELLER_2).size());
    }

    @Test
    void updateStatusChangesTheStoredStatus() throws Exception {
        long orderId = createOrder(BUYER_1, OrderStatus.CONFIRMED, line(PRODUCT_A, 1, "99.00"));

        boolean updated;
        try (Connection connection = dataSource.getConnection()) {
            updated = orderDao.updateStatus(connection, orderId, OrderStatus.SHIPPED);
        }

        assertTrue(updated);
        Order order = orderDao.findById(orderId).orElseThrow();
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        assertNotNull(order.getUpdatedAt());
    }

    @Test
    void updateStatusReturnsFalseForUnknownOrder() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertFalse(orderDao.updateStatus(connection, 9999L, OrderStatus.SHIPPED));
        }
    }

    @Test
    void findAllByStatusFiltersOrders() throws Exception {
        createOrder(BUYER_1, OrderStatus.CONFIRMED, line(PRODUCT_A, 1, "99.00"));
        createOrder(BUYER_1, OrderStatus.SHIPPED, line(PRODUCT_B, 1, "50.00"));
        createOrder(BUYER_2, OrderStatus.SHIPPED, line(PRODUCT_A, 1, "99.00"));

        List<Order> shipped = orderDao.findAllByStatus(OrderStatus.SHIPPED);

        assertEquals(2, shipped.size());
        assertTrue(shipped.stream().allMatch(o -> o.getStatus() == OrderStatus.SHIPPED));
        assertTrue(orderDao.findAllByStatus(OrderStatus.CANCELLED).isEmpty());
    }
}