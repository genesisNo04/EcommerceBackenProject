package com.example.EcommerceBackendProject.Entity.Payment;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class FakePaymentGateway implements PaymentGateway{

    @Override
    public PaymentResult charge(PaymentRequest paymentRequest) {
        boolean success = Math.random() > 0.2;

        if (!success) {
            return PaymentResult.failed();
        }

        return PaymentResult.success("FAKE-" + UUID.randomUUID());
    }
}
