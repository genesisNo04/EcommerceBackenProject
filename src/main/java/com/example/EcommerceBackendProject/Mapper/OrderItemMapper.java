package com.example.EcommerceBackendProject.Mapper;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.OrderItemResponseDTO;
import com.example.EcommerceBackendProject.Entity.OrderItem;

public class OrderItemMapper {

    public static OrderItemResponseDTO toDTO(OrderItem orderItem) {
        OrderItemResponseDTO orderItemResponseDTO = new OrderItemResponseDTO();
        orderItemResponseDTO.setProductId(orderItem.getProduct().getId());
        orderItemResponseDTO.setProductName(orderItem.getProduct().getProductName());
        orderItemResponseDTO.setQuantity(orderItem.getQuantity());
        orderItemResponseDTO.setPriceAtPurchase(orderItem.getPriceAtPurchase());

        return orderItemResponseDTO;
    }
}
