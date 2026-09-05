package com.mahashri.mahashrimart.dao;

import com.mahashri.mahashrimart.model.Review;

import java.sql.SQLException;
import java.util.List;

public interface ReviewDao {
    long create(long productId, long userId, int rating, String comment) throws SQLException;
    List<Review> findByProductId(long productId) throws SQLException;
    double averageRating(long productId) throws SQLException;
}