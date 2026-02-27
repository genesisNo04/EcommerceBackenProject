package com.example.EcommerceBackendProject.UnitTest.Utilities;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Mapper.ReviewMapper;

public class ReviewTestUtils {

    public static ReviewRequestDTO createTestReviewDto(int rating, String comment) {
        return new ReviewRequestDTO(rating, comment);
    }

    public static Review createTestReview(int rating, String comment) {
        return ReviewMapper.toEntity(new ReviewRequestDTO(rating, comment));
    }
}
