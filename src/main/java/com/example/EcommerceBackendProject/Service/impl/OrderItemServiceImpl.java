package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.Entity.OrderItem;
import com.example.EcommerceBackendProject.Repository.OrderItemRepository;
import com.example.EcommerceBackendProject.Repository.OrderRepository;
import com.example.EcommerceBackendProject.Service.OrderItemService;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public OrderItem addItemToOrder(Long orderId, Long productId, int quantity, Long userId) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be smaller than 0");
        }

        return null;
    }

    @Override
    public OrderItem updateOrderItemQuantity(OrderItem orderItemId, int quantity, Long userId) {
        return null;
    }

    @Override
    public Page<OrderItem> getOrderItems(Long orderId, Long userId, Pageable pageable) {
        return null;
    }

    @Override
    public Page<OrderItem> getAllOrderItemsForUser(Long userId, Pageable pageable) {
        return null;
    }
}
