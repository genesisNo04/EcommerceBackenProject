package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.PaymentRequestDTO;
import com.example.EcommerceBackendProject.Entity.Payment;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;


public interface PaymentService {

    Payment findPaymentByOrderIdAndUserId(Long orderId, Long userId);

    Payment createPayment(PaymentRequestDTO paymentRequestDTO, Long userId);

    void deletePayment(Long paymentId, Long userId);

    Payment updatePayment(Long paymentId, PaymentRequestDTO paymentRequestDTO, Long userId);

    Page<Payment> findPayments(Long userId, PaymentStatus status, PaymentType paymentType,LocalDateTime start, LocalDateTime end, Pageable pageable);
}
