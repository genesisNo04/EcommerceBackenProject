package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.PaymentRequestDTO;
import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;


public interface PaymentService {

    Payment findPaymentByOrderIdAndUserId(Long orderId, Long userId);

    void deletePayment(Long paymentId, Long userId);

    Payment updatePayment(Long paymentId, PaymentRequestDTO paymentRequestDTO, Long userId);

    Page<Payment> findPayments(Long userId, Long orderId, PaymentStatus status, PaymentType paymentType, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Payment processPayment(Long orderId, Long userId, PaymentType paymentType);
}
