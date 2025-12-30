package com.example.EcommerceBackendProject.DTO;

import com.example.EcommerceBackendProject.Entity.OrderItem;
import com.example.EcommerceBackendProject.Enum.Status;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponseDTO {

    private Long orderId;

    private BigDecimal totalAmount;

    private Status status;

    private List<OrderItemResponseDTO> orderItems;

    private Long paymentId;

    private LocalDateTime createdAt;
}
