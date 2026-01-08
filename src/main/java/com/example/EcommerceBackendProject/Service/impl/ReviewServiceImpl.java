package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.NoUserFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Mapper.ReviewMapper;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Repository.ReviewRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.ReviewService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public Review createReview(ReviewRequestDTO reviewRequestDTO, Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("User not found."));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found."));

        Review review = ReviewMapper.toEntity(reviewRequestDTO);
        review.setUser(user);
        review.setProduct(product);

        try {
            return reviewRepository.save(review);
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceAlreadyExistsException("Review already exists for this product");
        }
    }

    @Override
    public Page<Review> findByUserId(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<Review> findByProductId(Long productId, Pageable pageable) {
        return reviewRepository.findByProductId(productId, pageable);
    }

    @Override
    public Review findByUserIdAndProductId(Long userId, Long productId) {
        return reviewRepository.findByUserIdAndProductId(userId, productId).orElseThrow(() -> new NoResourceFoundException("No review found for this user on this product!"));
    }

    @Override
    public Page<Review> findByRatingAndProductId(int rating, Long productId, Pageable pageable) {
        return reviewRepository.findByRatingAndProductId(rating, productId, pageable);
    }

    @Override
    public Page<Review> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return reviewRepository.findByUserIdAndCreatedAtBetween(userId, start, end, pageable);
    }

    @Override
    public Page<Review> findByUserIdAndModifiedAtBetween(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return reviewRepository.findByUserIdAndModifiedAtBetween(userId, start, end, pageable);
    }

    @Override
    public Page<Review> findByProductIdAndCreatedAtBetween(Long productId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return reviewRepository.findByProductIdAndCreatedAtBetween(productId, start, end, pageable);
    }

    @Override
    public Page<Review> findByRating(int rating, Pageable pageable) {
        return reviewRepository.findByRating(rating, pageable);
    }

    @Override
    public Page<Review> findByRatingBetween(int startRating, int endRating, Pageable pageable) {
        return reviewRepository.findByRatingBetween(startRating, endRating, pageable);
    }

    @Override
    @Transactional
    public Review updateReview(ReviewRequestDTO reviewRequestDTO, Long reviewId, Long userId) {
        Review currentReview = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Review not found! or access denied"));

        currentReview.setRating(reviewRequestDTO.getRating());
        currentReview.setComment(reviewRequestDTO.getComment());
        return currentReview;
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review currentReview = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Review not found!"));
        reviewRepository.delete(currentReview);
    }
}
