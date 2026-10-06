package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.model.WishlistItem;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JdbcWishlistDaoTest {
    private static final long BUYER_1 = 3L;
    private static final long BUYER_2 = 4L;
    private static final long PRODUCT_A = 101L;
    private static final long PRODUCT_B = 201L;

    private JdbcWishlistDao dao;

    @BeforeEach
    void setUp() throws Exception {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:wishlistdaotest;DB_CLOSE_DELAY=-1");
        dataSource.setUser("sa");
        dataSource.setPassword("");

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("DROP ALL OBJECTS");
            runScript(statement, "db/schema.sql");
            statement.execute("INSERT INTO users (id, name, email, password_hash, role) VALUES (1, 'Seller One', 's1@test.com', 'x', 'SELLER')");
            statement.execute("INSERT INTO users (id, name, email, password_hash, role) VALUES (3, 'Buyer One', 'b1@test.com', 'x', 'BUYER')");
            statement.execute("INSERT INTO users (id, name, email, password_hash, role) VALUES (4, 'Buyer Two', 'b2@test.com', 'x', 'BUYER')");
            statement.execute("INSERT INTO products (id, seller_id, name, description, price, stock_qty, category) VALUES (101, 1, 'Wax Crayons', 'desc', 99.00, 10, 'Art Supplies')");
            statement.execute("INSERT INTO products (id, seller_id, name, description, price, stock_qty, category) VALUES (201, 1, 'Notebook', 'desc', 50.00, 0, 'Stationery')");
        }
        dao = new JdbcWishlistDao(dataSource);
    }

    private static void runScript(Statement statement, String resource) throws Exception {
        try (InputStream input = JdbcWishlistDaoTest.class.getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(input, "Missing resource " + resource);
            String script = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            for (String sql : Arrays.stream(script.split(";")).map(String::trim).filter(s -> !s.isBlank()).toList()) {
                statement.execute(sql);
            }
        }
    }

    @Test
    void newWishlistIsEmpty() throws Exception {
        assertTrue(dao.findByUserId(BUYER_1).isEmpty());
        assertFalse(dao.contains(BUYER_1, PRODUCT_A));
    }

    @Test
    void addThenFindReturnsProductDetails() throws Exception {
        dao.add(BUYER_1, PRODUCT_A);

        List<WishlistItem> items = dao.findByUserId(BUYER_1);
        assertEquals(1, items.size());
        assertEquals("Wax Crayons", items.get(0).getProductName());
        assertEquals("Art Supplies", items.get(0).getCategory());
        assertEquals(10, items.get(0).getAvailableStock());
        assertTrue(dao.contains(BUYER_1, PRODUCT_A));
    }

    @Test
    void newestItemComesFirst() throws Exception {
        dao.add(BUYER_1, PRODUCT_A);
        dao.add(BUYER_1, PRODUCT_B);

        List<WishlistItem> items = dao.findByUserId(BUYER_1);
        assertEquals(PRODUCT_B, items.get(0).getProductId());
        assertEquals(PRODUCT_A, items.get(1).getProductId());
    }

    @Test
    void removeDeletesOnlyThatEntry() throws Exception {
        dao.add(BUYER_1, PRODUCT_A);
        dao.add(BUYER_1, PRODUCT_B);

        dao.remove(BUYER_1, PRODUCT_A);

        assertFalse(dao.contains(BUYER_1, PRODUCT_A));
        assertTrue(dao.contains(BUYER_1, PRODUCT_B));
    }

    @Test
    void wishlistsAreSeparatePerUser() throws Exception {
        dao.add(BUYER_1, PRODUCT_A);

        assertTrue(dao.findByUserId(BUYER_2).isEmpty());
        assertFalse(dao.contains(BUYER_2, PRODUCT_A));
    }

    @Test
    void databaseRejectsDuplicateEntries() throws Exception {
        dao.add(BUYER_1, PRODUCT_A);

        assertThrows(SQLException.class, () -> dao.add(BUYER_1, PRODUCT_A));
    }
}
