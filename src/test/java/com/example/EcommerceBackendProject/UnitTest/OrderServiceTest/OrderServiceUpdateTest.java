package com.example.EcommerceBackendProject.UnitTest.OrderServiceTest;

import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.OrderItem;
import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Exception.InvalidOrderItemQuantityException;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.OrderItemsTestUtils.createOrderItemDto;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.OrderItemsTestUtils.createTestOrderItem;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.PaymentTestUtils.createTestPayment;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.createTestProduct;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.UserTestUtils.createTestUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceUpdateTest extends BaseOrderServiceTest{

    @Test
    void updateOrder() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);
        Product product1 = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 100, "testurl");
        product1.setId(2L);

        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setOrderItems(new HashSet<>(Set.of(
                createTestOrderItem(null, product, 5, BigDecimal.TEN)
        )));

        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(List.of(createOrderItemDto(1L, 1), createOrderItemDto(2L, 1)));

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product1));

        Order updatedOrder = orderService.updateOrder(orderRequestDTO, 1L, 1L);

        assertEquals(OrderStatus.CREATED, updatedOrder.getOrderStatus());
        assertEquals(BigDecimal.valueOf(999.98), updatedOrder.getTotalAmount());
        assertEquals(2, updatedOrder.getOrderItems().size());
        assertTrue(updatedOrder.getOrderItems()
                .stream()
                .anyMatch(item -> item.getProduct().getId().equals(1L)
                        && item.getProduct().getProductName().equals("PS5")
                        && item.getQuantity() == 1
                        && item.getPriceAtPurchase().equals(BigDecimal.valueOf(499.99))));
        assertTrue(updatedOrder.getOrderItems()
                .stream()
                .anyMatch(item -> item.getProduct().getId().equals(2L)
                        && item.getProduct().getProductName().equals("XBOX")
                        && item.getQuantity() == 1
                        && item.getPriceAtPurchase().equals(BigDecimal.valueOf(499.99))));

        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(productRepository).findById(1L);
        verify(productRepository).findById(2L);
    }

    @Test
    void updateOrder_orderNotFound() {
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(List.of(createOrderItemDto(1L, 1), createOrderItemDto(2L, 1)));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> orderService.updateOrder(orderRequestDTO, 1L, 1L));

        assertEquals("Order not found", ex.getMessage());

        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(productRepository, never()).findById(1L);
    }

    @Test
    void updateOrder_productNotFound() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.CREATED);
        order.setOrderItems(new HashSet<>(Set.of(
                createTestOrderItem(null, product, 5, BigDecimal.TEN)
        )));

        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(List.of(createOrderItemDto(1L, 1), createOrderItemDto(2L, 1)));

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> orderService.updateOrder(orderRequestDTO, 1L, 1L));

        assertEquals("No product found", ex.getMessage());

        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(productRepository).findById(1L);
    }

    @Test
    void updateOrder_invalidQuantity() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.CREATED);

        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(List.of(createOrderItemDto(1L, -1)));

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        InvalidOrderItemQuantityException ex = assertThrows(InvalidOrderItemQuantityException.class, () -> orderService.updateOrder(orderRequestDTO, 1L, 1L));

        assertEquals("Invalid quantity", ex.getMessage());

        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(productRepository).findById(1L);
    }

    @Test
    void updateOrder_insufficientStock() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 2, "testurl");
        product.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.CREATED);

        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(List.of(createOrderItemDto(1L, 5)));

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        InvalidOrderItemQuantityException ex = assertThrows(InvalidOrderItemQuantityException.class, () -> orderService.updateOrder(orderRequestDTO, 1L, 1L));

        assertEquals("Insufficient stock", ex.getMessage());

        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(productRepository).findById(1L);
    }

    @Test
    void updateOrder_orderStatusNotCreated() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 2, "testurl");
        product.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);

        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(List.of(createOrderItemDto(1L, 5)));

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.updateOrder(orderRequestDTO, 1L, 1L));

        assertEquals("Only CREATED orders can be updated", ex.getMessage());

        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(productRepository, never()).findById(1L);
    }
}
