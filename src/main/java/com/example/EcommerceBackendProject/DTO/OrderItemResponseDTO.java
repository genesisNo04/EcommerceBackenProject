package com.example.EcommerceBackendProject.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemResponseDTO {

    private Long productId;

    private String productName;

    private Integer quantity;

    private BigDecimal priceAtPurchase;
}
