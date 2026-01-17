package com.example.EcommerceBackendProject.Mapper;

import com.example.EcommerceBackendProject.DTO.OrderResponseDTO;
import com.example.EcommerceBackendProject.Entity.Order;

public class OrderMapper {

    public static OrderResponseDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        OrderResponseDTO orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setOrderId(order.getId());
        orderResponseDTO.setTotalAmount(order.getTotalAmount());
        orderResponseDTO.setOrderStatus(order.getOrderStatus());
        orderResponseDTO.setCreatedAt(order.getCreatedAt());

        if (order.getPayment() != null) {
            orderResponseDTO.setPaymentId(order.getPayment().getId());
        }

        if (order.getOrderItems() != null) {
            orderResponseDTO.setOrderItems(
                    order.getOrderItems().stream()
                            .map(OrderItemMapper::toDTO)
                            .toList()
            );
        }

        return orderResponseDTO;
    }
}
