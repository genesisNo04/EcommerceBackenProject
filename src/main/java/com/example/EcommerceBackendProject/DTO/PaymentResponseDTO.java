package com.example.EcommerceBackendProject.DTO;

import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponseDTO {

    private Long paymentId;

    private Long orderId;

    private PaymentType paymentType;

    private PaymentStatus status;

    private BigDecimal amount;

    private LocalDateTime createdAt;
}
