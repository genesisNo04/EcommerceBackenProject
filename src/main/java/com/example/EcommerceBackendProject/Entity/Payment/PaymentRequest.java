package com.example.EcommerceBackendProject.Entity.Payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PaymentRequest {

    private final Long orderId;
    private final BigDecimal amount;
}
