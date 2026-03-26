package com.example.EcommerceBackendProject.IntegrationTesting.Utilities;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;

import java.util.List;

public class OrderTestFactory {
    public static OrderRequestDTO createOrderDTO(List<OrderItemRequestDTO> items) {
        return new OrderRequestDTO(items);
    }
}
