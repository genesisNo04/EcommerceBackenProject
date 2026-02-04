package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.Entity.OrderItem;
import com.example.EcommerceBackendProject.Repository.OrderItemRepository;
import com.example.EcommerceBackendProject.Repository.OrderRepository;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Service.OrderItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    private ProductRepository productRepository;

    private static final Logger log = LoggerFactory.getLogger(OrderItemServiceImpl.class);

    @Override
    public Page<OrderItem> findOrderItems(Long orderId, Pageable pageable) {
        Page<OrderItem> items = orderItemRepository.findByOrderId(orderId, pageable);
        log.info("FETCH order items for [orderId={}] [total={}]", orderId, items.getTotalElements());
        return items;
    }

    @Override
    public Page<OrderItem> findAllOrderItemsForUser(Long userId, Pageable pageable) {
        Page<OrderItem> items = orderItemRepository.findByOrderUserId(userId, pageable);
        log.info("FETCH all order items for [userId={}] [total={}]", userId, items.getTotalElements());
        return items;
    }

    @Override
    public Page<OrderItem> findOrderItemsForUserInOrder(Long userId, Long orderId, Pageable pageable) {
        Page<OrderItem> items = orderItemRepository.findByOrderIdAndOrderUserId(orderId, userId, pageable);
        log.info("FETCH all order item of [userId={}] in [orderId={}] [total={}]", userId, orderId, items.getTotalElements());
        return items;
    }
}
