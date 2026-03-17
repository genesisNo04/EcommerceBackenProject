package com.example.EcommerceBackendProject.IntegrationTesting.Utilities;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;

public class OrderItemTestFactory {

    public static OrderItemRequestDTO createOrderItemDto(Long productId, int quantity) {
        return new OrderItemRequestDTO(productId, quantity);
    }
}
