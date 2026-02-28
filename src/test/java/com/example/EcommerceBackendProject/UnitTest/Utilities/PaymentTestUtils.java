package com.example.EcommerceBackendProject.UnitTest.Utilities;

import com.example.EcommerceBackendProject.DTO.PaymentRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;

import static com.example.EcommerceBackendProject.Entity.Payment.Payment.createPayment;

public class PaymentTestUtils {

    public static Payment createTestPayment(Order order, PaymentType paymentType, PaymentStatus status, String providerReference) {
        return createPayment(order, paymentType, providerReference, status);
    }

    public static PaymentRequestDTO createTestPayment(Long orderId, PaymentType paymentType) {
        return new PaymentRequestDTO(orderId, paymentType);
    }
}
