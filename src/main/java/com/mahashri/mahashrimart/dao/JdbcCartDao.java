package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.model.CartItem;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcCartDao extends JdbcDao implements CartDao {
    private static final String CART_QUERY = "SELECT c.id, c.user_id, c.product_id, p.name AS product_name, " +
            "p.image_url, p.price, c.quantity, p.stock_qty " +
            "FROM cart_items c JOIN products p ON p.id = c.product_id WHERE c.user_id = ? " +
            "ORDER BY c.id";

    public JdbcCartDao(DataSource dataSource) { super(dataSource); }

    @Override
    public List<CartItem> findByUserId(long userId) throws SQLException {
        try (Connection connection = connection()) {
            return findByUserId(connection, userId);
        }
    }

    @Override
    public void addItem(long userId, long productId, int quantity) throws SQLException {
        String update = "UPDATE cart_items SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?";
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(update)) {
            statement.setInt(1, quantity);
            statement.setLong(2, userId);
            statement.setLong(3, productId);
            if (statement.executeUpdate() == 0) {
                String insert = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";
                try (PreparedStatement insertStatement = connection.prepareStatement(insert)) {
                    insertStatement.setLong(1, userId);
                    insertStatement.setLong(2, productId);
                    insertStatement.setInt(3, quantity);
                    insertStatement.executeUpdate();
                }
            }
        }
    }

    @Override
    public void updateQuantity(long userId, long productId, int quantity) throws SQLException {
        String sql = "UPDATE cart_items SET quantity = ? WHERE user_id = ? AND product_id = ?";
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setLong(2, userId);
            statement.setLong(3, productId);
            statement.executeUpdate();
        }
    }

    @Override
    public void removeItem(long userId, long productId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE user_id = ? AND product_id = ?";
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, productId);
            statement.executeUpdate();
        }
    }

    @Override
    public List<CartItem> findByUserId(Connection connection, long userId) throws SQLException {
        List<CartItem> items = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(CART_QUERY)) {
            statement.setLong(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) items.add(map(result));
            }
        }
        return items;
    }

    @Override
    public void clear(Connection connection, long userId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.executeUpdate();
        }
    }

    private static CartItem map(ResultSet result) throws SQLException {
        CartItem item = new CartItem();
        item.setId(result.getLong("id"));
        item.setUserId(result.getLong("user_id"));
        item.setProductId(result.getLong("product_id"));
        item.setProductName(result.getString("product_name"));
        item.setImageUrl(result.getString("image_url"));
        item.setUnitPrice(result.getBigDecimal("price"));
        item.setQuantity(result.getInt("quantity"));
        item.setAvailableStock(result.getInt("stock_qty"));
        return item;
    }
}