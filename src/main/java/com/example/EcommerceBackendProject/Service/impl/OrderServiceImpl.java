package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.*;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Exception.InvalidOrderItemQuantityException;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.NoUserFoundException;
import com.example.EcommerceBackendProject.Repository.OrderRepository;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.OrderService;
import com.example.EcommerceBackendProject.Service.ShoppingCartService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ShoppingCartService shoppingCartService;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository, ShoppingCartService shoppingCartService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.shoppingCartService = shoppingCartService;
    }

    @Override
    @Transactional
    public Order createOrder(OrderRequestDTO orderRequestDTO, Long userId) {
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new NoUserFoundException("No user found!"));
        Order order = new Order();
        order.setOrderStatus(OrderStatus.IN_PROCESS);
        order.setUser(user);

        BigDecimal total = BigDecimal.ZERO;

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

        total = order.getOrderItems().stream()
                        .map(i -> i.getPriceAtPurchase().multiply(BigDecimal.valueOf(i.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        ShoppingCart cart = shoppingCartService.getCartOrThrow(userId);

        for (OrderItem orderItem : order.getOrderItems()) {
            cart.removeItemByProductId(orderItem.getProduct().getId());
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order updateOrder(OrderRequestDTO orderRequestDTO, Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.IN_PROCESS) {
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
    public Page<Order> findUserOrders(Long userId, OrderStatus orderStatus, LocalDateTime start, LocalDateTime end, Pageable pageable) {

        if (orderStatus != null && (start != null || end != null)) {
            return orderRepository.findByUserIdAndOrderStatusAndCreatedAtBetween(userId, orderStatus, start, end, pageable);
        }

        if (start != null || end != null) {
            return orderRepository.findByUserIdAndCreatedAtBetween(userId, start, end, pageable);
        }

        if (orderStatus != null) {
            return orderRepository.findByUserIdAndOrderStatus(userId, orderStatus, pageable);
        }

        return orderRepository.findByUserId(userId, pageable);
    }
}
