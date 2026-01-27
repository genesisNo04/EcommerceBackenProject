package com.example.EcommerceBackendProject.Entity.Payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentResult {

    private final boolean success;
    private final String referenceId;

    public static PaymentResult success(String referenceId) {
        return new PaymentResult(true, referenceId);
    }

    public static PaymentResult failed() {
        return new PaymentResult(false, null);
    }
}
