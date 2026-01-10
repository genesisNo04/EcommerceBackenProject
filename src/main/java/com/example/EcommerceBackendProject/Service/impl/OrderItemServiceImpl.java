package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.OrderItem;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.InvalidOrderItemQuantityException;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Repository.OrderItemRepository;
import com.example.EcommerceBackendProject.Repository.OrderRepository;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Service.OrderItemService;
import jakarta.transaction.Transactional;
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

    @Override
    @Transactional
    public OrderItem addItemToOrder(Long orderId, Long productId, int quantity, Long userId) {
        Order order = orderRepository.findByIdAndUserId(userId, orderId)
                .orElseThrow(() -> new NoResourceFoundException("No order found!"));


        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found!"));

        if (quantity <= 0 || quantity > product.getStockQuantity()) {
            throw new InvalidOrderItemQuantityException("Invalid quantity");
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(quantity);
        orderItem.setOrder(order);
        orderItem.setPriceAtPurchase(product.getPrice());
        orderItem.setProduct(product);
        order.getOrderItems().add(orderItem);
        orderItemRepository.save(orderItem);
        return orderItem;
    }

    @Override
    @Transactional
    public OrderItem updateOrderItemQuantity(Long orderItemId, Long orderId, int quantity, Long userId) {
        OrderItem orderItem = orderItemRepository.findByIdAndOrderIdAndOrderUserId(orderItemId, orderId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Item not found!"));

        Product product = orderItem.getProduct();

        if (quantity <= 0 || quantity > product.getStockQuantity()) {
            throw new InvalidOrderItemQuantityException("Invalid quantity");
        }

        orderItem.setQuantity(quantity);

        return orderItem;
    }

    @Override
    public Page<OrderItem> findOrderItems(Long orderId, Pageable pageable) {
        return orderItemRepository.findByOrderId(orderId, pageable);
    }

    @Override
    public Page<OrderItem> findAllOrderItemsForUser(Long userId, Pageable pageable) {
        return orderItemRepository.findByOrderUserId(userId, pageable);
    }

    @Override
    public Page<OrderItem> findOrderItemsForUserInOrder(Long userId, Long orderId, Pageable pageable) {
        return orderItemRepository.findByOrderIdAndOrderUserId(orderId, userId, pageable);
    }
}
