package com.example.EcommerceBackendProject.UnitTest.ReviewServiceTest;

import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Entity.User;
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
}
