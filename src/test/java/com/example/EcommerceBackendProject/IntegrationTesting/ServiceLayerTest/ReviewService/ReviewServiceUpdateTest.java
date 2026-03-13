package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.ReviewService;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.UserAccessDeniedException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.ReviewTestFactory;
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
public class ReviewServiceUpdateTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void updateReview_success() {
        Review review = testDataHelper.createReview(5, "Good product");

        ReviewRequestDTO reviewRequestDTO = ReviewTestFactory.createReviewRequestDto(4, "ok product");

        Review updatedReview = reviewService.updateReview(reviewRequestDTO, review.getId(), review.getUser().getId());

        Review savedReview = reviewRepository.findById(updatedReview.getId()).orElseThrow();

        assertEquals(4, savedReview.getRating());
        assertEquals("ok product", savedReview.getComment());
        assertEquals(review.getProduct().getId(), savedReview.getProduct().getId());
    }

    @Test
    void updateReview_failed_reviewNotFound() {
        ReviewRequestDTO reviewRequestDTO = ReviewTestFactory.createReviewRequestDto(4, "ok product");

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> reviewService.updateReview(reviewRequestDTO, 999L, 1L));

        assertEquals("Review not found", ex.getMessage());
    }

    @Test
    void updateReview_failed_userNotAuthorized() {
        Review review = testDataHelper.createReview(5, "Good product");

        ReviewRequestDTO reviewRequestDTO = ReviewTestFactory.createReviewRequestDto(4, "ok product");

        UserAccessDeniedException ex = assertThrows(UserAccessDeniedException.class, () -> reviewService.updateReview(reviewRequestDTO, review.getId(), 999L));

        assertEquals("Not authorized to update this review", ex.getMessage());
    }
}
