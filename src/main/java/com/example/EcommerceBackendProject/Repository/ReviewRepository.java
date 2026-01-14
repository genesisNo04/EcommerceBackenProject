package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByUserId(Long userId, Pageable pageable);

    Optional<Review> findByIdAndUserId(Long reviewId, Long userId);

    Page<Review> findByProductId(Long productId, Pageable pageable);

    Page<Review> findByRatingAndProductId(int rating, Long productId, Pageable pageable);

    Page<Review> findByRating(int rating, Pageable pageable);

    Page<Review> findByRatingBetween(int minRating, int maxRating, Pageable pageable);

    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);

    Page<Review> findByProductIdAndCreatedAtBetween(Long productId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Review> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Review> findByUserIdAndModifiedAtBetween(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Review> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
