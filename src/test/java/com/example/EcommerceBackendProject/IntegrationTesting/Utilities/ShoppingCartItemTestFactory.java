package com.example.EcommerceBackendProject.IntegrationTesting.Utilities;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.ShoppingCartItemUpdateRequestDTO;

public class ShoppingCartItemTestFactory {

    public static ShoppingCartItemRequestDTO createShoppingCartItemDto(Long productId, Integer quantity) {
        return new ShoppingCartItemRequestDTO(productId, quantity);
    }

    public static ShoppingCartItemUpdateRequestDTO createShoppingCartItemUpdateDto(Integer quantity) {
        return new ShoppingCartItemUpdateRequestDTO(quantity);
    }
}
