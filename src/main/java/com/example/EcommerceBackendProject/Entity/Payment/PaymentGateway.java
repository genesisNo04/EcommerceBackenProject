package com.example.EcommerceBackendProject.Entity.Payment;

public interface PaymentGateway {
    PaymentResult charge(PaymentRequest paymentRequest);
}
