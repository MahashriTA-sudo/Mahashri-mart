package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.model.WishlistItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcWishlistDao extends JdbcDao implements WishlistDao {
    private static final String LIST_QUERY = "SELECT w.id, w.user_id, w.product_id, p.name AS product_name, " +
            "p.image_url, p.category, p.price, p.stock_qty " +
            "FROM wishlist_items w JOIN products p ON p.id = w.product_id WHERE w.user_id = ? " +
            "ORDER BY w.id DESC";

    public JdbcWishlistDao(DataSource dataSource) { super(dataSource); }

    @Override
    public List<WishlistItem> findByUserId(long userId) throws SQLException {
        List<WishlistItem> items = new ArrayList<>();
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(LIST_QUERY)) {
            statement.setLong(1, userId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) items.add(map(result));
            }
        }
        return items;
    }

    @Override
    public boolean contains(long userId, long productId) throws SQLException {
        String sql = "SELECT 1 FROM wishlist_items WHERE user_id = ? AND product_id = ?";
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, productId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }

    @Override
    public void add(long userId, long productId) throws SQLException {
        String sql = "INSERT INTO wishlist_items (user_id, product_id) VALUES (?, ?)";
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, productId);
            statement.executeUpdate();
        }
    }

    @Override
    public void remove(long userId, long productId) throws SQLException {
        String sql = "DELETE FROM wishlist_items WHERE user_id = ? AND product_id = ?";
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, userId);
            statement.setLong(2, productId);
            statement.executeUpdate();
        }
    }

    private static WishlistItem map(ResultSet result) throws SQLException {
        WishlistItem item = new WishlistItem();
        item.setId(result.getLong("id"));
        item.setUserId(result.getLong("user_id"));
        item.setProductId(result.getLong("product_id"));
        item.setProductName(result.getString("product_name"));
        item.setImageUrl(result.getString("image_url"));
        item.setCategory(result.getString("category"));
        item.setUnitPrice(result.getBigDecimal("price"));
        item.setAvailableStock(result.getInt("stock_qty"));
        return item;
    }
}
