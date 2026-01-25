package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderIdAndOrderUserId(Long orderId, Long userid);

    Optional<Payment> findByIdAndOrderUserId(Long paymentId, Long userid);

    Page<Payment> findByStatusAndOrderUserId(PaymentStatus status, Long userid, Pageable pageable);

    Page<Payment> findByPaymentTypeAndOrderUserId(PaymentType type, Long userid, Pageable pageable);

    Page<Payment> findByStatusAndPaymentTypeAndOrderUserId(PaymentStatus status, PaymentType type, Long userid, Pageable pageable);

    Page<Payment> findByStatusAndPaymentTypeAndOrderUserIdAndCreatedAtBetween(PaymentStatus status, PaymentType type, Long userid, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Payment> findByCreatedAtBetweenAndOrderUserId(LocalDateTime start, LocalDateTime end, Long userid, Pageable pageable);

    Page<Payment> findByOrderUserId(Long userId, Pageable pageable);
}
