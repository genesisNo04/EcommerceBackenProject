package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface OrderService {

    Order createOrder(OrderRequestDTO orderRequestDTO, Long userId);

    Order updateOrder(OrderRequestDTO orderRequestDTO, Long orderId, Long userId);

    Order findOrderById(Long orderId, Long userId);

    void deleteOrder(Long orderId, Long userId);

    Page<Order> findAllOrders(Pageable pageable);

    Page<Order> findUserOrders(Long userId, OrderStatus orderStatus, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Order checkout(Long orderId, Long userId);

    Order checkout(Long orderId);

    void cancelOrder(Long orderId, Long userId);

    Page<Order> findByOrderId(Long orderId, Pageable pageable);

    void cancelOrder(Long orderId);

    Page<Order> findAllFiltered(Long userId, OrderStatus orderStatus, LocalDateTime start, LocalDateTime end, Pageable pageable);

}
