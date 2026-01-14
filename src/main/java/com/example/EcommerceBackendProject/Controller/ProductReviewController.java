package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;
import com.example.EcommerceBackendProject.DTO.ReviewResponseDTO;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Mapper.ReviewMapper;
import com.example.EcommerceBackendProject.Service.ReviewService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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

    @GetMapping
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByProductId(@PathVariable Long productId,
                                                                         @RequestParam(required = false) Integer rating,
                                                                         @RequestParam(required = false) LocalDate start,
                                                                         @RequestParam(required = false) LocalDate end,
                                                                         @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        pageable = pageableSortValidator.validate(pageable, SortableFields.REVIEW.getFields());

        LocalDateTime startTime = (start == null) ? LocalDateTime.MIN : LocalDateTime.of(start, LocalTime.MIDNIGHT);
        LocalDateTime endTime = (end == null) ? LocalDateTime.now() : LocalDateTime.of(end, LocalTime.MIDNIGHT.minusSeconds(1));

        Page<Review> reviews;

        if (rating != null) {
            reviews = reviewService.findByRatingAndProductId(rating, productId, pageable);
        } else if (start != null || end != null) {
            reviews = reviewService.findByProductIdAndCreatedAtBetween(productId, startTime, endTime, pageable);
        } else {
            reviews = reviewService.findByProductId(productId, pageable);
        }

        Page<ReviewResponseDTO> response = reviews.map(ReviewMapper::toDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ReviewResponseDTO> findReviewByProductIdAndUserId(@PathVariable Long productId, @PathVariable Long userId) {
        Review review = reviewService.findByUserIdAndProductId(userId, productId);
        return ResponseEntity.ok(ReviewMapper.toDTO(review));
    }
}
