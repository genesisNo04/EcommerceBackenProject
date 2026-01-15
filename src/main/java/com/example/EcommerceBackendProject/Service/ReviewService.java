package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;
import com.example.EcommerceBackendProject.Entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ReviewService {

    Review createReview(ReviewRequestDTO reviewRequestDTO, Long userId, Long productId);

    Review updateReview(ReviewRequestDTO reviewRequestDTO, Long reviewId, Long userId);

    void deleteReview(Long reviewId, Long userId);

    Page<Review> findReviews(Long userId, Long productId, Integer startRating, Integer endRating, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
