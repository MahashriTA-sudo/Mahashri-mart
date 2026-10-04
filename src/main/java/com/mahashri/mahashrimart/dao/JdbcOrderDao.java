package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.model.Order;
import com.mahashri.mahashrimart.model.OrderItem;
import com.mahashri.mahashrimart.model.OrderStatus;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcOrderDao extends JdbcDao implements OrderDao {
    public JdbcOrderDao(DataSource dataSource) { super(dataSource); }

    @Override
    public long create(Connection connection, Order order) throws SQLException {
        String sql = "INSERT INTO orders (buyer_id, status, total_amount) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, order.getBuyerId());
            statement.setString(2, order.getStatus().name());
            statement.setBigDecimal(3, order.getTotalAmount());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Order id was not returned.");
                return keys.getLong(1);
            }
        }
    }

    @Override
    public void addItem(Connection connection, long orderId, long productId, int quantity, java.math.BigDecimal unitPrice)
            throws SQLException {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            statement.setLong(2, productId);
            statement.setInt(3, quantity);
            statement.setBigDecimal(4, unitPrice);
            statement.executeUpdate();
        }
    }

    @Override
    public List<Order> findByBuyerId(long buyerId) throws SQLException {
        String orderSql = "SELECT id, buyer_id, status, total_amount, created_at, updated_at FROM orders " +
                "WHERE buyer_id = ? ORDER BY created_at DESC, id DESC";
        String itemSql = "SELECT oi.id, oi.order_id, oi.product_id, p.name AS product_name, oi.quantity, oi.unit_price " +
                "FROM order_items oi JOIN products p ON p.id = oi.product_id WHERE oi.order_id = ? ORDER BY oi.id";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = connection();
             PreparedStatement orderStatement = connection.prepareStatement(orderSql)) {
            orderStatement.setLong(1, buyerId);
            try (ResultSet result = orderStatement.executeQuery()) {
                while (result.next()) {
                    Order order = mapOrder(result);
                    order.setItems(findItems(connection, itemSql, order.getId()));
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    @Override
    public List<Order> findAll() throws SQLException {
        String orderSql = "SELECT id, buyer_id, status, total_amount, created_at, updated_at FROM orders " +
                "ORDER BY created_at DESC, id DESC";
        String itemSql = "SELECT oi.id, oi.order_id, oi.product_id, p.name AS product_name, oi.quantity, oi.unit_price " +
                "FROM order_items oi JOIN products p ON p.id = oi.product_id WHERE oi.order_id = ? ORDER BY oi.id";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = connection();
             PreparedStatement orderStatement = connection.prepareStatement(orderSql)) {
            try (ResultSet result = orderStatement.executeQuery()) {
                while (result.next()) {
                    Order order = mapOrder(result);
                    order.setItems(findItems(connection, itemSql, order.getId()));
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    @Override
    public List<Order> findBySellerId(long sellerId) throws SQLException {
        String orderSql = "SELECT DISTINCT o.id, o.buyer_id, o.status, o.total_amount, o.created_at, o.updated_at " +
                "FROM orders o " +
                "JOIN order_items oi ON oi.order_id = o.id " +
                "JOIN products p ON p.id = oi.product_id " +
                "WHERE p.seller_id = ? " +
                "ORDER BY o.created_at DESC, o.id DESC";
        String itemSql = "SELECT oi.id, oi.order_id, oi.product_id, p.name AS product_name, oi.quantity, oi.unit_price " +
                "FROM order_items oi JOIN products p ON p.id = oi.product_id " +
                "WHERE oi.order_id = ? AND p.seller_id = ? ORDER BY oi.id";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = connection();
             PreparedStatement orderStatement = connection.prepareStatement(orderSql)) {
            orderStatement.setLong(1, sellerId);
            try (ResultSet result = orderStatement.executeQuery()) {
                while (result.next()) {
                    Order order = mapOrder(result);
                    order.setItems(findItemsForSeller(connection, itemSql, order.getId(), sellerId));
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    @Override
    public Optional<Order> findById(long orderId) throws SQLException {
        String orderSql = "SELECT id, buyer_id, status, total_amount, created_at, updated_at FROM orders WHERE id = ?";
        String itemSql = "SELECT oi.id, oi.order_id, oi.product_id, p.name AS product_name, oi.quantity, oi.unit_price " +
                "FROM order_items oi JOIN products p ON p.id = oi.product_id WHERE oi.order_id = ? ORDER BY oi.id";
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(orderSql)) {
            statement.setLong(1, orderId);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) return Optional.empty();
                Order order = mapOrder(result);
                order.setItems(findItems(connection, itemSql, order.getId()));
                return Optional.of(order);
            }
        }
    }

    @Override
    public boolean updateStatus(Connection connection, long orderId, OrderStatus newStatus) throws SQLException {
        String sql = "UPDATE orders SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newStatus.name());
            statement.setLong(2, orderId);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    public List<Order> findAllByStatus(OrderStatus status) throws SQLException {
        String orderSql = "SELECT id, buyer_id, status, total_amount, created_at, updated_at FROM orders " +
                "WHERE status = ? ORDER BY created_at DESC, id DESC";
        String itemSql = "SELECT oi.id, oi.order_id, oi.product_id, p.name AS product_name, oi.quantity, oi.unit_price " +
                "FROM order_items oi JOIN products p ON p.id = oi.product_id WHERE oi.order_id = ? ORDER BY oi.id";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = connection();
             PreparedStatement orderStatement = connection.prepareStatement(orderSql)) {
            orderStatement.setString(1, status.name());
            try (ResultSet result = orderStatement.executeQuery()) {
                while (result.next()) {
                    Order order = mapOrder(result);
                    order.setItems(findItems(connection, itemSql, order.getId()));
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private static List<OrderItem> findItems(Connection connection, String sql, long orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    items.add(mapItem(result));
                }
            }
        }
        return items;
    }

    private static List<OrderItem> findItemsForSeller(Connection connection, String sql, long orderId, long sellerId)
            throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            statement.setLong(2, sellerId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    items.add(mapItem(result));
                }
            }
        }
        return items;
    }

    private static OrderItem mapItem(ResultSet result) throws SQLException {
        OrderItem item = new OrderItem();
        item.setId(result.getLong("id"));
        item.setOrderId(result.getLong("order_id"));
        item.setProductId(result.getLong("product_id"));
        item.setProductName(result.getString("product_name"));
        item.setQuantity(result.getInt("quantity"));
        item.setUnitPrice(result.getBigDecimal("unit_price"));
        return item;
    }

    private static Order mapOrder(ResultSet result) throws SQLException {
        Order order = new Order();
        order.setId(result.getLong("id"));
        order.setBuyerId(result.getLong("buyer_id"));
        order.setStatus(OrderStatus.valueOf(result.getString("status")));
        order.setTotalAmount(result.getBigDecimal("total_amount"));
        Timestamp createdAt = result.getTimestamp("created_at");
        if (createdAt != null) order.setCreatedAt(createdAt.toLocalDateTime());
        Timestamp updatedAt = result.getTimestamp("updated_at");
        if (updatedAt != null) order.setUpdatedAt(updatedAt.toLocalDateTime());
        return order;
    }
}