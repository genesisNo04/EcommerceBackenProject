package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.ReviewService;

import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Repository.ReviewRepository;
import com.example.EcommerceBackendProject.Service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("Test")
@Transactional
public class ReviewServiceReadTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void findReviews() {
        Product product = testDataHelper.createProduct();
        User user = testDataHelper.createUser();

        Product product1 = testDataHelper.createProduct();
        User user1 = testDataHelper.createUser();

        Review review = testDataHelper.createReview(5, "Good product review1", user, product);
        Review review1 = testDataHelper.createReview(4, "ok product review2", user, product);
        Review review2 = testDataHelper.createReview(5, "Good product review3", user1, product1);
        Review review3 = testDataHelper.createReview(2, "bad product review4", user1, product1);
        Review review4 = testDataHelper.createReview(5, "Good product review5", user, product1);
        Review review5 = testDataHelper.createReview(2, "bad product review6", user1, product);
        List<Long> ids = List.of(review.getId(), review1.getId(), review2.getId(), review3.getId(), review4.getId(), review5.getId());
        Pageable pageable = PageRequest.of(0, 10);

        Page<Review> reviews = reviewService.findReviews(null, null, null, null, null, null, pageable);

        assertEquals(6, reviews.getTotalElements());
        assertEquals(6, reviews.getContent().size());
        assertTrue(reviews.getContent().stream().map(Review::getId).collect(Collectors.toSet()).containsAll(ids));
    }
}
