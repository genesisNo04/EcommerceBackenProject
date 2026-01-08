package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.ReviewRequestDTO;
import com.example.EcommerceBackendProject.DTO.ReviewResponseDTO;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Exception.BadRequestException;
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
@RequestMapping("/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final PageableSortValidator pageableSortValidator;

    public ReviewController(ReviewService reviewService, PageableSortValidator pageableSortValidator) {
        this.reviewService = reviewService;
        this.pageableSortValidator = pageableSortValidator;
    }


    @GetMapping("/{userId}")
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByUserId(@PathVariable Long userId,
                                                                                  @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        pageable = pageableSortValidator.validate(pageable, SortableFields.REVIEW.getFields());
        Page<Review> reviews = reviewService.findByUserId(userId, pageable);
        Page<ReviewResponseDTO> response = reviews.map(ReviewMapper::toDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userid}/createdAt")
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByUserIdAndCreatedBetween(@PathVariable Long userId,
                                                                                       @RequestParam(required = false) LocalDate start,
                                                                                       @RequestParam(required = false) LocalDate end,
                                                                                  @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        LocalDateTime startTime = (start == null) ? LocalDateTime.MIN :  LocalDateTime.of(start, LocalTime.MIDNIGHT);
        LocalDateTime endTime = (end == null) ? LocalDateTime.now() : LocalDateTime.of(end, LocalTime.MIDNIGHT.minusSeconds(1));

        pageable = pageableSortValidator.validate(pageable, SortableFields.REVIEW.getFields());
        Page<Review> review = reviewService.findByUserIdAndCreatedAtBetween(userId, startTime, endTime, pageable);
        Page<ReviewResponseDTO> response = review.map(ReviewMapper::toDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userid}/modified")
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByUserIdAndModifiedBetween(@PathVariable Long userId, @PathVariable LocalDate start, @PathVariable LocalDate end,
                                                                                @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        LocalDateTime startTime = LocalDateTime.MIN;
        LocalDateTime endTime = LocalDateTime.now();

        if (start != null) {
            startTime =   LocalDateTime.of(start, LocalTime.MIDNIGHT);
        }

        if (end != null) {
            endTime = LocalDateTime.of(end, LocalTime.MIDNIGHT.minusSeconds(1));
        }

        pageable = pageableSortValidator.validate(pageable, SortableFields.REVIEW.getFields());
        Page<Review> review = reviewService.findByUserIdAndModifiedAtBetween(userId, startTime, endTime, pageable);
        Page<ReviewResponseDTO> response = review.map(ReviewMapper::toDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{rating}")
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByRating(@PathVariable int rating,
                                                                      @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        if (rating < 0 || rating > 5) {
            throw new BadRequestException("Rating can only be between 0-5");
        }
        pageable = pageableSortValidator.validate(pageable, SortableFields.REVIEW.getFields());
        Page<Review> review = reviewService.findByRating(rating, pageable);
        Page<ReviewResponseDTO> response = review.map(ReviewMapper::toDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByRatingBetween(@RequestParam int startRating, @RequestParam int endRating,
                                                                      @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        if (startRating < 0 || startRating > 5 || endRating < 0 || endRating > 5) {
            throw new BadRequestException("Rating can only be between 0-5");
        }
        pageable = pageableSortValidator.validate(pageable, SortableFields.REVIEW.getFields());
        Page<Review> review = reviewService.findByRatingBetween(startRating, endRating, pageable);
        Page<ReviewResponseDTO> response = review.map(ReviewMapper::toDTO);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reviewId}/{userId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(@PathVariable Long reviewId, @PathVariable Long userId, @RequestBody ReviewRequestDTO reviewRequestDTO) {
        Review review = reviewService.updateReview(reviewRequestDTO, reviewId, userId);
        return ResponseEntity.ok(ReviewMapper.toDTO(review));
    }

    @DeleteMapping("/{reviewId}/{userId}")
    public ResponseEntity<ReviewResponseDTO> deleteReview(@PathVariable Long reviewId, @PathVariable Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }
}
