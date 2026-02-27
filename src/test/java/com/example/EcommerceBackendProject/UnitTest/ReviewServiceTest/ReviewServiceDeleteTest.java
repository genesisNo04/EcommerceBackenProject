package com.example.EcommerceBackendProject.UnitTest.ReviewServiceTest;

import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.createTestProduct;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ReviewTestUtils.createTestReview;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewServiceDeleteTest extends BaseReviewServiceTest {

    @Test
    void deleteReview() {
        User user = new User();
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        Review review = createTestReview(5, "Good product");
        review.setId(1L);
        review.setUser(user);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(1L, 1L);

        verify(reviewRepository).delete(any(Review.class));
    }

    @Test
    void deleteReview_reviewNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> reviewService.deleteReview(1L, 1L));

        assertEquals("Review not found", ex.getMessage());

        verify(reviewRepository, never()).delete(any(Review.class));
    }

    @Test
    void deleteReview_userNotAuthorized() {
        User user = new User();
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        Review review = createTestReview(5, "Good product");
        review.setId(1L);
        review.setUser(user);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> reviewService.deleteReview(1L, 2L));

        assertEquals("Not authorized to delete this review", ex.getMessage());

        verify(reviewRepository, never()).delete(any(Review.class));
    }

    @Test
    void deleteReview_adminService() {
        Review review = createTestReview(5, "Good product");
        review.setId(1L);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(1L);

        verify(reviewRepository).delete(any(Review.class));
    }

    @Test
    void deleteReview_adminService_noReviewFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> reviewService.deleteReview(1L));

        assertEquals("Review not found", ex.getMessage());

        verify(reviewRepository, never()).delete(any(Review.class));
    }
}
