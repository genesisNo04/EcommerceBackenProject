package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.*;
import com.example.EcommerceBackendProject.Mapper.ReviewMapper;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Repository.ReviewRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.ReviewService;
import com.example.EcommerceBackendProject.Specification.ReviewSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    private static final Logger log = LoggerFactory.getLogger(ReviewServiceImpl.class);

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
            reviewRepository.save(review);

            log.info("CREATED review [reviewId={}] for product [productId={}]", review.getId(), productId);

            return review;
        } catch (DataIntegrityViolationException ex) {
            throw new ResourceAlreadyExistsException("Review already exists for this product");
        }
    }

    @Override
    @Transactional
    public Review updateReview(ReviewRequestDTO reviewRequestDTO, Long reviewId, Long userId) {
        Review currentReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoResourceFoundException("Review not found!"));

        if (!currentReview.getUser().getId().equals(userId)) {
            throw new UserAccessDeniedException("Not authorized to update this review");
        }

        currentReview.setRating(reviewRequestDTO.getRating());
        currentReview.setComment(reviewRequestDTO.getComment());

        log.info("UPDATED review [reviewId={}]", currentReview.getId());

        return currentReview;
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review currentReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoResourceFoundException("Review not found!"));

        if (!currentReview.getUser().getId().equals(userId)) {
            throw new IllegalStateException("Not authorized to delete this review");
        }

        reviewRepository.delete(currentReview);
        log.info("DELETED review [reviewId={}] for user [targetUserId={}]", currentReview.getId(), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Review> findReviews(Long userId, Long productId, Integer startRating, Integer endRating, LocalDateTime start, LocalDateTime end, Pageable pageable) {

        Specification<Review> spec = (root, query, cb) -> cb.conjunction();

        if (userId != null) {
            spec = spec.and(ReviewSpecification.hasUserId(userId));
        }

        if (productId != null) {
            spec = spec.and(ReviewSpecification.hasProductId(productId));
        }

        if (startRating != null || endRating != null) {

            if (startRating != null && (startRating < 0 || startRating > 5)) {
                throw new BadRequestException("startRating must be between 0 and 5");
            }

            if (endRating != null && (endRating < 0 || endRating > 5)) {
                throw new BadRequestException("endRating must be between 0 and 5");
            }

            if (startRating != null && endRating != null && startRating > endRating) {
                throw new BadRequestException("startRating cannot be larger than endRating");
            }

            int min = startRating != null ? startRating : 0;
            int max = endRating != null ? endRating : 5;
            spec = spec.and(ReviewSpecification.ratingBetween(min, max));
        }

        spec = spec.and(ReviewSpecification.createdBetween(start, end));
        Page<Review> reviews = reviewRepository.findAll(spec, pageable);
        log.info("FETCHED reviews [total={}] for user [targetUserId={}] product [productId={}] ratingRange=[{}-{}]", reviews.getTotalElements(), userId, productId, startRating, endRating);

        return reviews;
    }

    @Override
    public void deleteReview(Long reviewId) {
        Review currentReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NoResourceFoundException("Review not found!"));

        reviewRepository.delete(currentReview);
        log.info("DELETED review [reviewId={}]", currentReview.getId());
    }
}
