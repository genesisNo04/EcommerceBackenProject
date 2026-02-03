package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByOrderIdAndOrderUserId(Long orderId, Long userid);

    Optional<Payment> findByIdAndOrderUserId(Long paymentId, Long userid);

//    Page<Payment> findByStatusAndOrderUserId(PaymentStatus status, Long userid, Pageable pageable);
//
//    Page<Payment> findByPaymentTypeAndOrderUserId(PaymentType type, Long userid, Pageable pageable);
//
//    Page<Payment> findByStatusAndPaymentTypeAndOrderUserId(PaymentStatus status, PaymentType type, Long userid, Pageable pageable);
//
//    Page<Payment> findByStatusAndPaymentTypeAndOrderUserIdAndCreatedAtBetween(PaymentStatus status, PaymentType type, Long userid, LocalDateTime start, LocalDateTime end, Pageable pageable);
//
//    Page<Payment> findByCreatedAtBetweenAndOrderUserId(LocalDateTime start, LocalDateTime end, Long userid, Pageable pageable);
//
//    Page<Payment> findByOrderUserId(Long userId, Pageable pageable);
}
