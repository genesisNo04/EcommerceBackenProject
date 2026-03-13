package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.ReviewService;

import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Repository.ReviewRepository;
import com.example.EcommerceBackendProject.Service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("Test")
@Transactional
public class ReviewServiceDeleteTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void deleteReview_success() {
        Review review = testDataHelper.createReview(5, "Good product");

        reviewService.deleteReview(review.getId(), review.getUser().getId());

        Review savedReviewed = reviewRepository.findById(review.getId()).orElse(null);

        assertNull(savedReviewed);
    }

    @Test
    void deleteReview_failed_noReviewFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> reviewService.deleteReview(999L, 999L));

        assertEquals("Review not found", ex.getMessage());
    }

    @Test
    void deleteReview_failed_userNotAuthorized() {
        Review review = testDataHelper.createReview(5, "Good product");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> reviewService.deleteReview(review.getId(), 999L));

        assertEquals("Not authorized to delete this review", ex.getMessage());
    }

    @Test
    void deleteReview_success_forAdmin() {
        Review review = testDataHelper.createReview(5, "Good product");

        reviewService.deleteReview(review.getId());

        Review savedReviewed = reviewRepository.findById(review.getId()).orElse(null);

        assertNull(savedReviewed);
    }

    @Test
    void deleteReview_failed_noReviewFound_forAdmin() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> reviewService.deleteReview(999L));

        assertEquals("Review not found", ex.getMessage());
    }
}
