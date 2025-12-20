package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.Entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ReviewService {

    Review createReview(Review review, Long userId);

    Page<Review> findByUserId(Long userId, Pageable pageable);

    Page<Review> findByProductId(Long productId, Pageable pageable);

    Page<Review> findByUserIdAndProductId(Long userId, Long productId, Pageable pageable);

    Page<Review> findByRatingAndProductId(int rating, Long productId, Pageable pageable);

    Page<Review> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Review> findByUserIdAndModifiedAtBetween(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Review> findByProductIdAndCreatedAtBetween(Long productId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Review> findByRating(int rating, Pageable pageable);

    Page<Review> findByRatingBetween(int startRating, int endRating, Pageable pageable);

    Review updateReview(Review review, Long userId);

    void deleteReview(Long reviewId, Long userId);
}
