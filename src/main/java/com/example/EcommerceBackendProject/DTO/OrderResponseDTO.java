package com.example.EcommerceBackendProject.DTO;

import com.example.EcommerceBackendProject.Enum.OrderStatus;
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

    private OrderStatus orderStatus;

    private List<OrderItemResponseDTO> orderItems;

    private Long paymentId;

    private LocalDateTime createdAt;
}
