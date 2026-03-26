package com.example.EcommerceBackendProject.IntegrationTesting.Utilities;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;

public class ReviewTestFactory {

    public static ReviewRequestDTO createReviewRequestDto(int rating, String comment) {
        return new ReviewRequestDTO(rating, comment);
    }
}
