package com.mahashri.mahashrimart.service;

import com.mahashri.mahashrimart.dao.ReviewDao;
import com.mahashri.mahashrimart.exception.ValidationException;
import com.mahashri.mahashrimart.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {
    private ReviewDao reviewDao;
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewDao = mock(ReviewDao.class);
        reviewService = new ReviewService(reviewDao);
    }

    @Test
    void submitSavesAValidReview() throws Exception {
        when(reviewDao.create(1L, 2L, 5, "Great product")).thenReturn(7L);

        long id = reviewService.submit(1L, 2L, 5, "Great product");

        assertEquals(7L, id);
        verify(reviewDao).create(1L, 2L, 5, "Great product");
    }

    @Test
    void submitAcceptsTheLowestAndHighestRating() throws Exception {
        reviewService.submit(1L, 2L, 1, "ok");
        reviewService.submit(1L, 2L, 5, "ok");

        verify(reviewDao).create(1L, 2L, 1, "ok");
        verify(reviewDao).create(1L, 2L, 5, "ok");
    }

    @Test
    void submitRejectsRatingBelowOne() throws Exception {
        assertThrows(ValidationException.class, () -> reviewService.submit(1L, 2L, 0, "bad"));
        verify(reviewDao, never()).create(anyLong(), anyLong(), anyInt(), any());
    }

    @Test
    void submitRejectsRatingAboveFive() throws Exception {
        assertThrows(ValidationException.class, () -> reviewService.submit(1L, 2L, 6, "wow"));
        verify(reviewDao, never()).create(anyLong(), anyLong(), anyInt(), any());
    }

    @Test
    void submitTrimsTheComment() throws Exception {
        reviewService.submit(1L, 2L, 4, "   nice one   ");

        verify(reviewDao).create(1L, 2L, 4, "nice one");
    }

    @Test
    void submitStoresNullWhenCommentIsBlank() throws Exception {
        reviewService.submit(1L, 2L, 4, "     ");

        verify(reviewDao).create(1L, 2L, 4, null);
    }

    @Test
    void submitStoresNullWhenCommentIsNull() throws Exception {
        reviewService.submit(1L, 2L, 3, null);

        verify(reviewDao).create(1L, 2L, 3, null);
    }

    @Test
    void forProductReturnsTheReviewsFromTheDao() throws Exception {
        List<Review> reviews = List.of(new Review());
        when(reviewDao.findByProductId(1L)).thenReturn(reviews);

        assertSame(reviews, reviewService.forProduct(1L));
    }

    @Test
    void averageRatingReturnsTheValueFromTheDao() throws Exception {
        when(reviewDao.averageRating(1L)).thenReturn(4.5);

        assertEquals(4.5, reviewService.averageRating(1L), 0.0001);
    }
}
