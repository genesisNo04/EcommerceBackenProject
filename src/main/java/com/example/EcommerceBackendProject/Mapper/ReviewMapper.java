package com.example.EcommerceBackendProject.Mapper;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;
import com.example.EcommerceBackendProject.DTO.ReviewResponseDTO;
import com.example.EcommerceBackendProject.Entity.Review;

public class ReviewMapper {

    public static ReviewResponseDTO toDTO(Review review) {
        if (review == null) {
            return null;
        }

        ReviewResponseDTO reviewResponseDTO = new ReviewResponseDTO();
        reviewResponseDTO.setProductId(review.getProduct().getId());
        reviewResponseDTO.setUserId(review.getUser().getId());
        reviewResponseDTO.setUsername(review.getUser().getUsername());
        reviewResponseDTO.setProductName(review.getProduct().getProductName());
        reviewResponseDTO.setRating(review.getRating());
        reviewResponseDTO.setComment(review.getComment());
        reviewResponseDTO.setCreatedAt(review.getCreatedAt());
        return reviewResponseDTO;
    }

    public static Review toEntity(ReviewRequestDTO reviewRequestDTO) {
        Review review = new Review();
        review.setRating(reviewRequestDTO.getRating());
        review.setComment(reviewRequestDTO.getComment());
        return review;
    }
}
