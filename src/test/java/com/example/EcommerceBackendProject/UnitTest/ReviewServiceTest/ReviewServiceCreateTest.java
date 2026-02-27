package com.example.EcommerceBackendProject.UnitTest.ReviewServiceTest;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.createTestProduct;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ReviewTestUtils.createTestReviewDto;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.UserTestUtils.createTestUser;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ReviewServiceCreateTest extends BaseReviewServiceTest{

    @Test
    void createReview() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        ReviewRequestDTO requestDTO = createTestReviewDto(5, "good product");

        when(userRepository.findById(1L)). thenReturn(Optional.of(user));
        when(productRepository.findById(1L)). thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review review = reviewService.createReview(requestDTO, 1L, 1L);

        assertEquals(5, review.getRating());
        assertEquals("good product", review.getComment());
        assertSame(user, review.getUser());
        assertSame(product, review.getProduct());

        verify(userRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_userNotFound() {
        ReviewRequestDTO requestDTO = createTestReviewDto(5, "good product");
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> reviewService.createReview(requestDTO, 1L, 1L));

        assertEquals("No user found", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(productRepository, never()).findById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_productNotFound() {
        User user = new User();
        when(userRepository.findById(1L)). thenReturn(Optional.of(user));
        ReviewRequestDTO requestDTO = createTestReviewDto(5, "good product");
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> reviewService.createReview(requestDTO, 1L, 1L));

        assertEquals("No product found", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReview_reviewAlreadyExist() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        ReviewRequestDTO requestDTO = createTestReviewDto(5, "good product");

        when(userRepository.findById(1L)). thenReturn(Optional.of(user));
        when(productRepository.findById(1L)). thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenThrow(new DataIntegrityViolationException(""));

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> reviewService.createReview(requestDTO, 1L, 1L));

        assertEquals("Review already exists for this product",ex.getMessage());

        verify(userRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(reviewRepository).save(any(Review.class));
    }
}
