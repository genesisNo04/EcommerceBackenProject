package com.example.EcommerceBackendProject.Specification;

import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Entity.Review;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class PaymentSpecification {

    private PaymentSpecification() {}

    public static Specification<Payment> hasUserId(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("order").get("user").get("id"), userId);
    }

    public static Specification<Payment> hasOrderId(Long orderId) {
        return (root, query, cb) -> cb.equal(root.get("order").get("id"), orderId);
    }

    public static Specification<Payment> hasStatus(PaymentStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Payment> hasPaymentType(PaymentType paymentType) {
        return (root, query, cb) -> cb.equal(root.get("paymentType"), paymentType);
    }

    public static Specification<Payment> createdBetween(LocalDateTime start, LocalDateTime end) {

        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("createdAt"), start, end);
            }

            if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), start);
            }

            if (end != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), end);
            }

            return cb.conjunction();
        };
    }
}
