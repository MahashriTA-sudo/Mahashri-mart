package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.ReviewDao;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.Review;

import java.sql.SQLException;
import java.util.List;

public class ReviewService {
    private final ReviewDao reviewDao;

    public ReviewService(ReviewDao reviewDao) { this.reviewDao = reviewDao; }

    public long submit(long productId, long userId, int rating, String comment) throws SQLException, ValidationException {
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Rating must be between 1 and 5.");
        }
        String cleanComment = comment == null ? "" : comment.trim();
        return reviewDao.create(productId, userId, rating, cleanComment.isBlank() ? null : cleanComment);
    }

    public List<Review> forProduct(long productId) throws SQLException {
        return reviewDao.findByProductId(productId);
    }

    public double averageRating(long productId) throws SQLException {
        return reviewDao.averageRating(productId);
    }
}