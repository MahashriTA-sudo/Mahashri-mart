package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.dto.ProductRequest;
import com.mahashri.mahashrimart.model.Product;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcProductDao extends JdbcDao implements ProductDao {
    public JdbcProductDao(DataSource dataSource) { super(dataSource); }

    @Override
    public List<Product> findAll() throws SQLException {
        String sql = "SELECT p.id, p.seller_id, u.name AS seller_name, p.name, p.description, p.price, " +
                "p.stock_qty, p.category, p.image_url, p.created_at " +
                "FROM products p JOIN users u ON u.id = p.seller_id ORDER BY p.created_at DESC, p.id DESC";
        List<Product> products = new ArrayList<>();
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) products.add(map(result));
        }
        return products;
    }

    @Override
    public Optional<Product> findById(long id) throws SQLException {
        String sql = "SELECT p.id, p.seller_id, u.name AS seller_name, p.name, p.description, p.price, " +
                "p.stock_qty, p.category, p.image_url, p.created_at " +
                "FROM products p JOIN users u ON u.id = p.seller_id WHERE p.id = ?";
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        }
    }

    @Override
    public long create(long sellerId, ProductRequest request) throws SQLException {
        String sql = "INSERT INTO products (seller_id, name, description, price, stock_qty, category, image_url) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, sellerId);
            statement.setString(2, request.name());
            statement.setString(3, request.description());
            statement.setBigDecimal(4, request.price());
            statement.setInt(5, request.stockQty());
            statement.setString(6, request.category());
            statement.setString(7, request.imageUrl().isBlank() ? null : request.imageUrl());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Product id was not returned.");
                return keys.getLong(1);
            }
        }
    }

    @Override
    public Optional<Product> findByIdForUpdate(Connection connection, long id) throws SQLException {
        String sql = "SELECT p.id, p.seller_id, u.name AS seller_name, p.name, p.description, p.price, " +
                "p.stock_qty, p.category, p.image_url, p.created_at " +
                "FROM products p JOIN users u ON u.id = p.seller_id WHERE p.id = ? FOR UPDATE";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        }
    }

    @Override
    public boolean decrementStock(Connection connection, long id, int quantity) throws SQLException {
        String sql = "UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, quantity);
            statement.setLong(2, id);
            statement.setInt(3, quantity);
            return statement.executeUpdate() == 1;
        }
    }

    static Product map(ResultSet result) throws SQLException {
        Product product = new Product();
        product.setId(result.getLong("id"));
        product.setSellerId(result.getLong("seller_id"));
        product.setSellerName(result.getString("seller_name"));
        product.setName(result.getString("name"));
        product.setDescription(result.getString("description"));
        product.setPrice(result.getBigDecimal("price"));
        product.setStockQty(result.getInt("stock_qty"));
        product.setCategory(result.getString("category"));
        product.setImageUrl(result.getString("image_url"));
        Timestamp createdAt = result.getTimestamp("created_at");
        if (createdAt != null) product.setCreatedAt(createdAt.toLocalDateTime());
        return product;
    }
}