package com.example.EcommerceBackendProject.UnitTest.ReviewServiceTest;

import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.ReviewTestUtils.createTestReview;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewServiceReadTest extends BaseReviewServiceTest{

    @Test
    void searchReview() {
        Pageable pageable = PageRequest.of(0, 10);
        Review review = createTestReview(5, "Good product");
        review.setId(1L);
        Review review1 = createTestReview(4, "OK product");
        review1.setId(2L);
        Review review2 = createTestReview(3, "not bad product");
        review2.setId(3L);
        Review review3 = createTestReview(2, "bad product");
        review3.setId(4L);

        List<Review> reviews = List.of(review, review1, review2, review3);
        Page<Review> returnReview = new PageImpl<>(reviews, pageable, reviews.size());

        when(reviewRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(returnReview);

        Page<Review> result = reviewService.findReviews(1L, 1L, 1, 5, LocalDateTime.now().minusDays(5), LocalDateTime.now(), pageable);

        assertEquals(4, result.getContent().size());
        assertEquals(reviews, result.getContent());

        verify(reviewRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void searchReview_startRatingOutOfRange() {

        Pageable pageable = PageRequest.of(0, 10);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> reviewService.findReviews(null, null, -1, 5, null, null, pageable));

        assertEquals("startRating must be between 0 and 5", ex.getMessage());

        verify(reviewRepository, never()).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void searchReview_endRatingOutOfRange() {

        Pageable pageable = PageRequest.of(0, 10);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> reviewService.findReviews(null, null, 2, 6, null, null, pageable));

        assertEquals("endRating must be between 0 and 5", ex.getMessage());
        verify(reviewRepository, never()).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void searchReview_startRatingLargerThanEndRating() {
        Pageable pageable = PageRequest.of(0, 10);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> reviewService.findReviews(null, null, 5, 4, null, null, pageable));

        assertEquals("startRating cannot be larger than endRating", ex.getMessage());
        verify(reviewRepository, never()).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void searchReview_startDateLaterThanEndDate() {
        Pageable pageable = PageRequest.of(0, 10);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> reviewService.findReviews(null, null, null, null, LocalDateTime.now(), LocalDateTime.now().minusDays(1), pageable));

        assertEquals("Start date cannot be later than End date", ex.getMessage());
        verify(reviewRepository, never()).findAll(any(Specification.class), eq(pageable));
    }
}
