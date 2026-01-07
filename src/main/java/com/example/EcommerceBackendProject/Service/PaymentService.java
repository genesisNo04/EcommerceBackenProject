package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.PaymentRequestDTO;
import com.example.EcommerceBackendProject.Entity.Payment;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Enum.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public interface PaymentService {

    Payment findPaymentByOrderIdAndUserId(Long orderId, Long userId);

    Page<Payment> findPaymentByStatusAndUserId(PaymentStatus status, Long userId, Pageable pageable);

    Page<Payment> findPaymentByPaymentTypeAndUserId(PaymentType paymentType, Long userId, Pageable pageable);

    Page<Payment> findPaymentByPaymentTypeAndStatusAndUserId(PaymentType paymentType, PaymentStatus status, Long userId, Pageable pageable);

    Page<Payment> findPaymentByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Long userId, Pageable pageable);

    Payment createPayment(PaymentRequestDTO paymentRequestDTO, Long userId);

    void deletePayment(Long paymentId, Long userId);

    Payment updatePayment(Long paymentId, PaymentRequestDTO paymentRequestDTO, Long userId);

    Page<Payment> findPaymentByUserId(Long userId, Pageable pageable);
}
