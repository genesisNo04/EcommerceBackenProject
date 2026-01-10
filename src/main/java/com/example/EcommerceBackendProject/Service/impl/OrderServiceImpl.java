package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.OrderItem;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.Status;
import com.example.EcommerceBackendProject.Exception.InvalidOrderItemQuantityException;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.NoUserFoundException;
import com.example.EcommerceBackendProject.Repository.OrderRepository;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.OrderItemService;
import com.example.EcommerceBackendProject.Service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ProductRepository productRepository;

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
    public Order createOrder(OrderRequestDTO orderRequestDTO, Long userId) {
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new NoUserFoundException("No user found!"));
        Order order = new Order();
        order.setStatus(Status.IN_PROCESS);
        order.setUser(user);

        order = orderRepository.save(order);

        for (var itemDto : orderRequestDTO.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new NoResourceFoundException("No product found!"));

            if (itemDto.getQuantity() <= 0 || itemDto.getQuantity() > product.getStockQuantity()) {
                throw new InvalidOrderItemQuantityException("Invalid quantity");
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPriceAtPurchase(product.getPrice());

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            order.getOrderItems().add(item);
        }

        BigDecimal total = order.getOrderItems().stream()
                        .map(i -> i.getPriceAtPurchase().multiply(BigDecimal.valueOf(i.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);
        return order;
    }

    @Override
    @Transactional
    public Order updateOrder(OrderRequestDTO orderRequestDTO, Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Order not found"));

        if (order.getStatus() != Status.IN_PROCESS) {
            throw new IllegalStateException("Only IN_PROCESS orders can be updated");
        }

        if (order.getPayment() != null) {
            throw new IllegalStateException("Cannot modify a paid order");
        }

        // 1. Restore stock for existing items
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }

        // 2. Clear existing items (orphanRemoval deletes them)
        order.getOrderItems().clear();

        // 3. Re-add items from request
        for (var itemDto : orderRequestDTO.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new NoResourceFoundException("No product found"));

            if (itemDto.getQuantity() <= 0 || itemDto.getQuantity() > product.getStockQuantity()) {
                throw new InvalidOrderItemQuantityException("Invalid quantity");
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPriceAtPurchase(product.getPrice());

            product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
            order.getOrderItems().add(item);
        }

        // 4. Recalculate total
        BigDecimal total = order.getOrderItems().stream()
                .map(i -> i.getPriceAtPurchase().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);

        return order;
    }

    @Override
    public Order findOrderById(Long orderId, Long userId) {
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

    @Override
    public Page<Order> findUserOrders(Long userId, Status status, LocalDateTime start, LocalDateTime end, Pageable pageable) {

        if (status != null && (start != null || end != null)) {
            return orderRepository.findByUserIdAndStatusAndCreatedAtBetween(userId, status, start, end, pageable);
        }

        if (start != null || end != null) {
            return orderRepository.findByUserIdAndCreatedAtBetween(userId, start, end, pageable);
        }

        if (status != null) {
            return orderRepository.findByUserIdAndStatus(userId, status, pageable);
        }

        return orderRepository.findByUserId(userId, pageable);
    }
}
