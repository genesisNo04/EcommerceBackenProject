package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.Status;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.NoUserFoundException;
import com.example.EcommerceBackendProject.Repository.OrderRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<Order> findOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<Order> findOrdersBetween(Long userId, Pageable pageable, LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByUserIdAndCreatedAtBetween(userId, start, end, pageable);
    }

    @Override
    public Page<Order> findByStatus(Long userId, Status status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    @Override
    @Transactional
    public Order createOrder(Order order, Long userId) {
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new NoUserFoundException("No user found!"));
        order.setUser(user);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrder(Order orderUpdated, Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Order not found"));
        order.setOrderItems(orderUpdated.getOrderItems());
        order.setTotalAmount(orderUpdated.getTotalAmount());
        order.setStatus(orderUpdated.getStatus());
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId, Long userId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Order not found!"));
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Order not found!"));
        orderRepository.delete(order);
    }

    @Override
    public Page<Order> findAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
}
