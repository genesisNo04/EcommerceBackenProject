package com.example.EcommerceBackendProject.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class ShoppingCartResponseDTO {

    private Long cartId;

    private BigDecimal totalAmount;

    private Long userId;

    private Set<ShoppingCartItemResponseDTO> items;
}
