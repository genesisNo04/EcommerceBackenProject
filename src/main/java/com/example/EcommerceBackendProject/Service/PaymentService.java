package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.Entity.Payment;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Enum.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;


public interface PaymentService {

    Payment findPaymentByOrderIdAndUserId(Long orderId, Long userId);

    Page<Payment> findPaymentByStatusAndUserId(Status status, Long userId, Pageable pageable);

    Page<Payment> findPaymentByPaymentTypeAndUserId(PaymentType paymentType, Long userId, Pageable pageable);

    Page<Payment> findPaymentByPaymentTypeAndStatusAndUserId(PaymentType paymentType, Status status, Long userId, Pageable pageable);

    Page<Payment> findPaymentByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Long userId, Pageable pageable);

    Payment createPayment(Payment payment, Long orderId, Long userId);

    void deletePayment(Long paymentId, Long userId);

    Payment updatePayment(Long paymentId, Payment updatedPayment, Long userId);

    Page<Payment> findPaymentByUserId(Long userId, Pageable pageable);
}
