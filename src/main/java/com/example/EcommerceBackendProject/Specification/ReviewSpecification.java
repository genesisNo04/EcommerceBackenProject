package com.example.EcommerceBackendProject.Specification;

import com.example.EcommerceBackendProject.Entity.Review;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ReviewSpecification {

    private ReviewSpecification() {}

    public static Specification<Review> hasUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Review> hasProductId(Long productId) {
        return (root, query, cb) -> cb.equal(root.get("product").get("id"), productId);
    }

    public static Specification<Review> ratingBetween(int min, int max) {
        return (root, query, cb) -> cb.between(root.get("rating"), min, max);
    }

    public static Specification<Review> createdBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> cb.between(root.get("createdAt"), start, end);
    }


}
