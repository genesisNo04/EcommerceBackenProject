package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;
import com.example.EcommerceBackendProject.DTO.ReviewResponseDTO;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Mapper.ReviewMapper;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.ReviewService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/users/reviews")
@PreAuthorize("hasRole('USER')")
public class UserReviewController {

    private final ReviewService reviewService;
    private final PageableSortValidator pageableSortValidator;
    private final SecurityUtils securityUtils;

    public UserReviewController(ReviewService reviewService, PageableSortValidator pageableSortValidator, SecurityUtils securityUtils) {
        this.reviewService = reviewService;
        this.pageableSortValidator = pageableSortValidator;
        this.securityUtils = securityUtils;
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(@PathVariable Long reviewId, @Valid @RequestBody ReviewRequestDTO reviewRequestDTO) {
        Long userId = securityUtils.getCurrentUserId();
        Review review = reviewService.updateReview(reviewRequestDTO, reviewId, userId);
        return ResponseEntity.ok(ReviewMapper.toDTO(review));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> deleteReview(@PathVariable Long reviewId) {
        Long userId = securityUtils.getCurrentUserId();
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponseDTO>> findReviews(@RequestParam(required = false) Long productId,
                                                               @RequestParam(required = false) Integer startRating,
                                                               @RequestParam(required = false) Integer endRating,
                                                               @RequestParam(required = false) LocalDate start,
                                                               @RequestParam(required = false) LocalDate end,
                                                               @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        Long userId = securityUtils.getCurrentUserId();
        pageable = pageableSortValidator.validate(pageable, SortableFields.REVIEW.getFields());

        LocalDateTime startTime = (start == null) ? LocalDateTime.of(1970, 1, 1, 0, 0) :  start.atStartOfDay();
        LocalDateTime endTime = (end == null) ? LocalDateTime.now() : end.plusDays(1).atStartOfDay();

        Page<Review> review = reviewService.findReviews(userId, productId, startRating, endRating, startTime, endTime, pageable);
        Page<ReviewResponseDTO> response = review.map(ReviewMapper::toDTO);
        return ResponseEntity.ok(response);
    }
}
