package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.Entity.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderItemService {

    OrderItem addItemToOrder(Long orderId, Long productId, int quantity, Long userId);

    OrderItem updateOrderItemQuantity(OrderItem orderItemId, int quantity, Long userId);

    Page<OrderItem> getOrderItems(Long orderId, Long userId, Pageable pageable);

    Page<OrderItem> getAllOrderItemsForUser(Long userId, Pageable pageable);
}
