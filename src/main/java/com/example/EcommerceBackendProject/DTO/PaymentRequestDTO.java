package com.example.EcommerceBackendProject.DTO;

import com.example.EcommerceBackendProject.Enum.PaymentType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class PaymentRequestDTO {

    @NotNull
    private Long orderId;

    @NotNull
    private PaymentType paymentType;
}
