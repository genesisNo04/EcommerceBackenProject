package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Enum.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OrderService {

    Page<Order> findOrdersByUserId(Long userId, Pageable pageable);

    Page<Order> findOrdersBetween(Long userId, Pageable pageable, LocalDateTime start, LocalDateTime end);

    Page<Order> findByStatus(Long userId, Status status, Pageable pageable);

    Order createOrder(OrderRequestDTO orderRequestDTO, Long userId);

    Order updateOrder(OrderRequestDTO orderRequestDTO, Long orderId, Long userId);

    Order findOrderById(Long orderId, Long userId);

    void deleteOrder(Long orderId, Long userId);

    Page<Order> findAllOrders(Pageable pageable);

    Page<Order> findUserOrders(Long userId, Status status, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
