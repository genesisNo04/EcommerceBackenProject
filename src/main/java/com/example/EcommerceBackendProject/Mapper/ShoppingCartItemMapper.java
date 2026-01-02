package com.example.EcommerceBackendProject.Mapper;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemResponseDTO;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;

import java.math.BigDecimal;

public class ShoppingCartItemMapper {

    public static ShoppingCartItemResponseDTO toDTO(ShoppingCartItem shoppingCartItem) {
        if (shoppingCartItem == null) {
            return null;
        }

        ShoppingCartItemResponseDTO shoppingCartItemResponseDTO = new ShoppingCartItemResponseDTO();
        shoppingCartItemResponseDTO.setProductId(shoppingCartItem.getProduct().getId());
        shoppingCartItemResponseDTO.setProductName(shoppingCartItem.getProduct().getProductName());
        shoppingCartItemResponseDTO.setQuantity(shoppingCartItem.getQuantity());

        return shoppingCartItemResponseDTO;
    }
}
