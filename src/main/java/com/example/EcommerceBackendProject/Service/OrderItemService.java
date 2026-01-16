package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;
import com.example.EcommerceBackendProject.Entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderItemService {


    Page<OrderItem> findOrderItems(Long orderId, Pageable pageable);

    Page<OrderItem> findAllOrderItemsForUser(Long userId, Pageable pageable);

    Page<OrderItem> findOrderItemsForUserInOrder(Long userId, Long orderId, Pageable pageable);
}
