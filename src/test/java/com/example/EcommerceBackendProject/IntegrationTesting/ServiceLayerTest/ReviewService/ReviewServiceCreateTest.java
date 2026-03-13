package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.ReviewService;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
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
public class ReviewServiceCreateTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void createReview_success() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ReviewRequestDTO reviewRequestDTO = ReviewTestFactory.createReviewRequestDto(5, "Good product");

        Review createdReview = reviewService.createReview(reviewRequestDTO, user.getId(), product.getId());

        Review savedReview = reviewRepository.findById(createdReview.getId()).orElseThrow();

        assertEquals(5, savedReview.getRating());
        assertEquals("Good product", savedReview.getComment());
        assertEquals(savedReview.getProduct().getId(), product.getId());
    }

    @Test
    void createReview_failed_userNotFound() {
        Product product = testDataHelper.createProduct();

        ReviewRequestDTO reviewRequestDTO = ReviewTestFactory.createReviewRequestDto(5, "Good product");

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> reviewService.createReview(reviewRequestDTO, 999L, product.getId()));

        assertEquals("No user found", ex.getMessage());
    }

    @Test
    void createReview_failed_productNotFound() {
        User user = testDataHelper.createUser();

        ReviewRequestDTO reviewRequestDTO = ReviewTestFactory.createReviewRequestDto(5, "Good product");

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> reviewService.createReview(reviewRequestDTO, user.getId(), 999L));

        assertEquals("No product found", ex.getMessage());
    }

    @Test
    void createReview_failed_moreThanOneReview() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        ReviewRequestDTO reviewRequestDTO = ReviewTestFactory.createReviewRequestDto(5, "Good product");
        ReviewRequestDTO reviewRequestDTO1 = ReviewTestFactory.createReviewRequestDto(5, "Good product");

        reviewService.createReview(reviewRequestDTO, user.getId(), product.getId());
        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> reviewService.createReview(reviewRequestDTO1, user.getId(), product.getId()));

        assertEquals("Review already exists for this product", ex.getMessage());
    }
}
