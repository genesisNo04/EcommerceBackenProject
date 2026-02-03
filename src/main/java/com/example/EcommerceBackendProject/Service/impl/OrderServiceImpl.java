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
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    public Order updateOrder(OrderRequestDTO orderRequestDTO, Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Order not found"));

        if (order.getOrderStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED orders can be updated");
        }

        order.getOrderItems().clear();

        BigDecimal total = BigDecimal.ZERO;

        for (var itemDto : orderRequestDTO.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new NoResourceFoundException("No product found"));

            if (itemDto.getQuantity() <= 0) {
                throw new InvalidOrderItemQuantityException("Invalid quantity");
            }

            if (itemDto.getQuantity() > product.getStockQuantity()) {
                throw new InvalidOrderItemQuantityException("Insufficient stock");
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPriceAtPurchase(product.getPrice());

            total = total.add(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())));
            order.getOrderItems().add(item);

        }

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
        if (order.getOrderStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Only CREATED orders can be deleted");
        }
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

    @Override
    public Page<Order> findAllFiltered(Long userId, OrderStatus orderStatus, LocalDateTime start, LocalDateTime end, Pageable pageable) {

        if (userId != null) {
            return findUserOrders(userId, orderStatus, start, end, pageable);
        }

        // Global admin filters
        if (orderStatus != null && (start != null || end != null)) {
            return orderRepository.findByOrderStatusAndCreatedAtBetween(
                    orderStatus, start, end, pageable);
        }

        if (start != null || end != null) {
            return orderRepository.findByCreatedAtBetween(start, end, pageable);
        }

        if (orderStatus != null) {
            return orderRepository.findByOrderStatus(orderStatus, pageable);
        }

        return orderRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Order createOrder(OrderRequestDTO orderRequestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found!"));

        Order order = new Order(user);

        BigDecimal total = BigDecimal.ZERO;

        for (var itemDto : orderRequestDTO.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new NoResourceFoundException("No product found!"));

            if (itemDto.getQuantity() <= 0) {
                throw new InvalidOrderItemQuantityException("Invalid quantity");
            }

            if (itemDto.getQuantity() > product.getStockQuantity()) {
                throw new InvalidOrderItemQuantityException("Insufficient stock");
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(itemDto.getQuantity());
            item.setPriceAtPurchase(product.getPrice());

            total = total.add(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())));

            order.getOrderItems().add(item);
        }

        order.setTotalAmount(total);
        order.setOrderStatus(OrderStatus.CREATED);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public Order checkout(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Order not found"));

        return checkout(order);
    }

    @Override
    @Transactional
    public Order checkout(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoResourceFoundException("Order not found"));

        return checkout(order);
    }

    private Order checkout(Order order) {
        if (order.getOrderStatus() == OrderStatus.PENDING_PAYMENT) {
            return order;
        }

        if (order.getOrderStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Order cannot be checked out");
        }

        Long userId = order.getUser().getId();
        BigDecimal total = BigDecimal.ZERO;

        order.markPendingPayment();

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();

            if (item.getQuantity() > product.getStockQuantity()) {
                throw new InvalidOrderItemQuantityException("Insufficient stock for product " + product.getId());
            }

            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            total = total.add(item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        ShoppingCart cart = shoppingCartService.getCartOrThrow(userId);

        for (OrderItem orderItem : order.getOrderItems()) {
            cart.removeItemByProductId(orderItem.getProduct().getId());
        }

        order.setTotalAmount(total);
        return order;
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Order not found"));

        cancelOrderInternally(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoResourceFoundException("Order not found"));

        cancelOrderInternally(order);
    }

    private void cancelOrderInternally(Order order) {
        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT && order.getOrderStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Order cannot be canceled");
        }

        if (order.getOrderStatus() == OrderStatus.PENDING_PAYMENT) {
            for (OrderItem orderItem : order.getOrderItems()) {
                Product product = orderItem.getProduct();
                product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
            }
        }

        order.markCanceled();
    }

    @Override
    public Page<Order> findByOrderId(Long orderId, Pageable pageable) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoResourceFoundException("No order found"));
        return new PageImpl<>(List.of(order), pageable, 1);
    }
}
