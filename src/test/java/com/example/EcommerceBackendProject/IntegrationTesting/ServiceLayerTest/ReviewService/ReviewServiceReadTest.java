package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.ReviewService;

import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.BadRequestException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Repository.ReviewRepository;
import com.example.EcommerceBackendProject.Service.ReviewService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
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

    @Autowired
    private EntityManager entityManager;

    @Test
    void findReviews() {
        Product product = testDataHelper.createProduct();
        Product product1 = testDataHelper.createProduct();
        Product product2 = testDataHelper.createProduct();
        Product product3 = testDataHelper.createProduct();

        User user = testDataHelper.createUser("testuser", "test123", "testuser@gmail.com");
        User user1 = testDataHelper.createUser("testuser1", "test123", "testuser1@gmail.com");

        Review review = testDataHelper.createReview(5, "Good product review1", user, product);
        Review review1 = testDataHelper.createReview(4, "ok product review2", user, product1);
        Review review2 = testDataHelper.createReview(5, "Good product review3", user, product2);
        Review review3 = testDataHelper.createReview(2, "bad product review4", user, product3);
        Review review4 = testDataHelper.createReview(2, "bad product review6", user1, product);
        Review review5 = testDataHelper.createReview(5, "Good product review5", user1, product1);
        Review review6 = testDataHelper.createReview(5, "Good product review5", user1, product2);
        Review review7 = testDataHelper.createReview(5, "Good product review5", user1, product3);
        List<Long> ids = List.of(review.getId(), review1.getId(), review2.getId(), review3.getId(), review4.getId(), review5.getId(), review6.getId(), review7.getId());
        Pageable pageable = PageRequest.of(0, 10);

        Page<Review> reviews = reviewService.findReviews(null, null, null, null, null, null, pageable);

        assertEquals(8, reviews.getTotalElements());
        assertEquals(8, reviews.getContent().size());
        assertTrue(reviews.getContent().stream().map(Review::getId).collect(Collectors.toSet()).containsAll(ids));
    }

    @Test
    void findReviews_findWithCriteria() {
        Product product = testDataHelper.createProduct();
        Product product1 = testDataHelper.createProduct();
        Product product2 = testDataHelper.createProduct();
        Product product3 = testDataHelper.createProduct();

        User user = testDataHelper.createUser("testuser", "test123", "testuser@gmail.com");
        User user1 = testDataHelper.createUser("testuser1", "test123", "testuser1@gmail.com");

        Review review = testDataHelper.createReview(5, "Good product review1", user, product);
        Review review1 = testDataHelper.createReview(4, "ok product review2", user, product1);
        Review review2 = testDataHelper.createReview(5, "Good product review3", user, product2);
        Review review3 = testDataHelper.createReview(2, "bad product review4", user, product3);
        Review review4 = testDataHelper.createReview(2, "bad product review6", user1, product);
        Review review5 = testDataHelper.createReview(5, "Good product review5", user1, product1);
        Review review6 = testDataHelper.createReview(5, "Good product review5", user1, product2);
        Review review7 = testDataHelper.createReview(5, "Good product review5", user1, product3);
        List<Long> ids = List.of(review.getId(), review1.getId(), review2.getId());
        Pageable pageable = PageRequest.of(0, 10);

        Page<Review> reviews = reviewService.findReviews(user.getId(), null, 4, 5, null, null, pageable);

        assertEquals(3, reviews.getTotalElements());
        assertEquals(3, reviews.getContent().size());
        assertTrue(reviews.getContent().stream().map(Review::getId).collect(Collectors.toSet()).containsAll(ids));
    }

    @Test
    void findReviews_failedEndRatingSmallerThanStartRating() {
        Pageable pageable = PageRequest.of(0, 10);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> reviewService.findReviews(null, null, 4, 3, null, null, pageable));

        assertEquals("startRating cannot be larger than endRating", ex.getMessage());
    }

    @Test
    void findReviews_startRatingLargerThan5() {
        Pageable pageable = PageRequest.of(0, 10);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> reviewService.findReviews(null, null, 6, null, null, null, pageable));

        assertEquals("startRating must be between 0 and 5", ex.getMessage());
    }

    @Test
    void findReviews_startRatingSmallerThan0() {
        Pageable pageable = PageRequest.of(0, 10);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> reviewService.findReviews(null, null, -1, null, null, null, pageable));

        assertEquals("startRating must be between 0 and 5", ex.getMessage());
    }

    @Test
    void findReviews_endRatingLargerThan5() {
        Pageable pageable = PageRequest.of(0, 10);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> reviewService.findReviews(null, null, null, 6, null, null, pageable));

        assertEquals("endRating must be between 0 and 5", ex.getMessage());
    }

    @Test
    void findReviews_endRatingSmallerThan0() {
        Pageable pageable = PageRequest.of(0, 10);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> reviewService.findReviews(null, null, null, -1, null, null, pageable));

        assertEquals("endRating must be between 0 and 5", ex.getMessage());
    }

    @Test
    void findReviews_endDateSmallerThanStartDate() {
        Pageable pageable = PageRequest.of(0, 10);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> reviewService.findReviews(null, null, null, null, LocalDateTime.now(), LocalDateTime.now().minusDays(2), pageable));

        assertEquals("Start date cannot be later than End date", ex.getMessage());
    }
}
