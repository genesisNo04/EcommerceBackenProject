package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;
import com.example.EcommerceBackendProject.DTO.ReviewResponseDTO;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Mapper.ReviewMapper;
import com.example.EcommerceBackendProject.Service.ReviewService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/products/{productId}/reviews")
public class ProductReviewController {

    private final ReviewService reviewService;
    private final PageableSortValidator pageableSortValidator;

    public ProductReviewController(ReviewService reviewService, PageableSortValidator pageableSortValidator) {
        this.reviewService = reviewService;
        this.pageableSortValidator = pageableSortValidator;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ReviewResponseDTO> createReview(@PathVariable Long productId, @PathVariable Long userId, @RequestBody ReviewRequestDTO reviewRequestDTO) {
        Review review = reviewService.createReview(reviewRequestDTO, userId, productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReviewMapper.toDTO(review));
    }
}
