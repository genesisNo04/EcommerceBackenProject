package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;
import com.example.EcommerceBackendProject.DTO.ReviewResponseDTO;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Mapper.ReviewMapper;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.ReviewService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/products/{productId}/reviews")
@PreAuthorize("hasRole('USER')")
public class ProductReviewController {

    private final ReviewService reviewService;
    private final PageableSortValidator pageableSortValidator;

    public ProductReviewController(ReviewService reviewService, PageableSortValidator pageableSortValidator) {
        this.reviewService = reviewService;
        this.pageableSortValidator = pageableSortValidator;
    }

    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(@PathVariable Long productId, @RequestBody ReviewRequestDTO reviewRequestDTO) {
        Long userId = SecurityUtils.getCurrentUserId();
        Review review = reviewService.createReview(reviewRequestDTO, userId, productId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ReviewMapper.toDTO(review));
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponseDTO>> getReview(@PathVariable Long productId, @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        pageable = pageableSortValidator.validate(pageable, SortableFields.REVIEW.getFields());

        LocalDateTime startTime = LocalDateTime.of(1970, 1, 1, 0, 0);
        LocalDateTime endTime = LocalDateTime.now();

        Page<Review> review = reviewService.findReviews(null, productId, 0, 5, startTime, endTime, pageable);
        Page<ReviewResponseDTO> response = review.map(ReviewMapper::toDTO);
        return ResponseEntity.ok(response);
    }
}
