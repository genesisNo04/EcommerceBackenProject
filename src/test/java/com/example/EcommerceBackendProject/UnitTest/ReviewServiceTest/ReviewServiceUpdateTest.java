package com.example.EcommerceBackendProject.UnitTest.ReviewServiceTest;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.UserAccessDeniedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.createTestProduct;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ReviewTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewServiceUpdateTest extends BaseReviewServiceTest{

    @Test
    void updateReview() {
        User user = new User();
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        Review review = createTestReview(5, "Good product");
        review.setId(1L);
        review.setUser(user);

        ReviewRequestDTO requestDTO = createTestReviewDto(3, "good enough product");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        Review updateReview = reviewService.updateReview(requestDTO, 1L, 1L);

        assertEquals(3, updateReview.getRating());
        assertEquals("good enough product", updateReview.getComment());
        assertSame(user, review.getUser());

        verify(reviewRepository).findById(1L);
    }

    @Test
    void updateReview_userNotAuthorize() {
        User user = new User();
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);
        Review review = createTestReview(5, "Good product");
        review.setId(1L);
        review.setUser(user);

        ReviewRequestDTO requestDTO = createTestReviewDto(3, "good enough product");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        UserAccessDeniedException ex = assertThrows(UserAccessDeniedException.class, () -> reviewService.updateReview(requestDTO, 1L, 2L));

        assertEquals("Not authorized to update this review", ex.getMessage());

        verify(reviewRepository).findById(1L);
    }

    @Test
    void updateReview_reviewNotFound() {
        ReviewRequestDTO requestDTO = createTestReviewDto(3, "good enough product");
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> reviewService.updateReview(requestDTO, 1L, 2L));

        assertEquals("Review not found", ex.getMessage());

        verify(reviewRepository).findById(1L);
    }
}
