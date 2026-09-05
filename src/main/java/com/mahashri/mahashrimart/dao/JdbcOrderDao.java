package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.model.Order;
import com.mahashri.mahashrimart.model.OrderItem;
import com.mahashri.mahashrimart.model.OrderStatus;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
        String orderSql = "SELECT id, buyer_id, status, total_amount, created_at FROM orders " +
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

    private static List<OrderItem> findItems(Connection connection, String sql, long orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, orderId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(result.getLong("id"));
                    item.setOrderId(result.getLong("order_id"));
                    item.setProductId(result.getLong("product_id"));
                    item.setProductName(result.getString("product_name"));
                    item.setQuantity(result.getInt("quantity"));
                    item.setUnitPrice(result.getBigDecimal("unit_price"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    private static Order mapOrder(ResultSet result) throws SQLException {
        Order order = new Order();
        order.setId(result.getLong("id"));
        order.setBuyerId(result.getLong("buyer_id"));
        order.setStatus(OrderStatus.valueOf(result.getString("status")));
        order.setTotalAmount(result.getBigDecimal("total_amount"));
        Timestamp createdAt = result.getTimestamp("created_at");
        if (createdAt != null) order.setCreatedAt(createdAt.toLocalDateTime());
        return order;
    }
}