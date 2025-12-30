package com.example.EcommerceBackendProject.Mapper;

import com.example.EcommerceBackendProject.DTO.PaymentResponseDTO;
import com.example.EcommerceBackendProject.Entity.Payment;

public class PaymentMapper {

    public static PaymentResponseDTO toDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO();
        paymentResponseDTO.setPaymentId(payment.getId());
        paymentResponseDTO.setOrderId(payment.getOrder().getId());
        paymentResponseDTO.setPaymentType(payment.getPaymentType());
        paymentResponseDTO.setStatus(payment.getStatus());
        paymentResponseDTO.setAmount(payment.getAmount());
        paymentResponseDTO.setCreatedAt(payment.getCreatedAt());
        return paymentResponseDTO;
    }
}
