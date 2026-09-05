package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.model.Review;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcReviewDao extends JdbcDao implements ReviewDao {
    public JdbcReviewDao(DataSource dataSource) { super(dataSource); }

    @Override
    public long create(long productId, long userId, int rating, String comment) throws SQLException {
        String sql = "INSERT INTO reviews (product_id, user_id, rating, comment) VALUES (?, ?, ?, ?)";
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, productId);
            statement.setLong(2, userId);
            statement.setInt(3, rating);
            statement.setString(4, comment);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Review id was not returned.");
                return keys.getLong(1);
            }
        }
    }

    @Override
    public List<Review> findByProductId(long productId) throws SQLException {
        String sql = "SELECT r.id, r.product_id, r.user_id, u.name AS user_name, r.rating, r.comment, r.created_at " +
                "FROM reviews r JOIN users u ON u.id = r.user_id " +
                "WHERE r.product_id = ? ORDER BY r.created_at DESC, r.id DESC";
        List<Review> reviews = new ArrayList<>();
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) reviews.add(map(result));
            }
        }
        return reviews;
    }

    @Override
    public double averageRating(long productId) throws SQLException {
        String sql = "SELECT AVG(rating) AS avg_rating FROM reviews WHERE product_id = ?";
        try (Connection connection = connection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, productId);
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    double avg = result.getDouble("avg_rating");
                    return result.wasNull() ? 0.0 : avg;
                }
                return 0.0;
            }
        }
    }

    static Review map(ResultSet result) throws SQLException {
        Review review = new Review();
        review.setId(result.getLong("id"));
        review.setProductId(result.getLong("product_id"));
        review.setUserId(result.getLong("user_id"));
        review.setUserName(result.getString("user_name"));
        review.setRating(result.getInt("rating"));
        review.setComment(result.getString("comment"));
        Timestamp createdAt = result.getTimestamp("created_at");
        if (createdAt != null) review.setCreatedAt(createdAt.toLocalDateTime());
        return review;
    }
}