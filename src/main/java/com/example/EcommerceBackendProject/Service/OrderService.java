package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Enum.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OrderService {

    Order createOrder(OrderRequestDTO orderRequestDTO, Long userId);

    Order updateOrder(OrderRequestDTO orderRequestDTO, Long orderId, Long userId);

    Order findOrderById(Long orderId, Long userId);

    void deleteOrder(Long orderId, Long userId);

    Page<Order> findAllOrders(Pageable pageable);

    Page<Order> findUserOrders(Long userId, Status status, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
