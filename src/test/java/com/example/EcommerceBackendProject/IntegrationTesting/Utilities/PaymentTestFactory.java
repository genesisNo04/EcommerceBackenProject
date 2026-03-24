package com.example.EcommerceBackendProject.IntegrationTesting.Utilities;

import com.example.EcommerceBackendProject.DTO.PaymentRequestDTO;
import com.example.EcommerceBackendProject.Enum.PaymentType;

public class PaymentTestFactory {

    public static PaymentRequestDTO createPaymentDTO(Long orderId, PaymentType paymentType) {
        return new PaymentRequestDTO(orderId, paymentType);
    }
}
