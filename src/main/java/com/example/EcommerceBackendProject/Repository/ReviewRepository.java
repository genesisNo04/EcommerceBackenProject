package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByUserId(Long userId, Pageable pageable);

    Page<Review> findByProductId(Long productId, Pageable pageable);

    Page<Review> findByRatingAndProductId(int rating, Long productId, Pageable pageable);

    Page<Review> findByProductIdAndCreatedAtBetween(Long productId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Review> findByProductIdAndModifiedAtBetween(Long productId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Review> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Review> findByModifiedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Review> findByRating(int rating, Pageable pageable);

    Page<Review> findByRatingBetween(int minRating, int maxRating, Pageable pageable);
}
