package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ShoppingCartItemResponseDTO {

    private Long productId;

    private String productName;

    private Integer quantity;

    private BigDecimal priceSnapshot;

    private BigDecimal lineTotal;
}
