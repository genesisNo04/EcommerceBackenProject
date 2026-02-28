package com.example.EcommerceBackendProject.UnitTest.OrderServiceTest;

import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.OrderItem;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.OrderItemsTestUtils.*;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceCancelTest extends BaseOrderServiceTest {

    @Test
    void cancelOrder_byUser() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        OrderItem item = createTestOrderItem(order, product, 2, BigDecimal.valueOf(499.99));
        item.setProduct(product);
        order.setOrderItems(Set.of(item));

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L, 1L);

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        assertEquals(102, product.getStockQuantity());

        verify(orderRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    void cancelOrder() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        OrderItem item = createTestOrderItem(order, product, 2, BigDecimal.valueOf(499.99));
        item.setProduct(product);
        order.setOrderItems(Set.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        assertEquals(102, product.getStockQuantity());

        verify(orderRepository).findById(1L);
    }

    @Test
    void cancelOrder_notCorrectStatus() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.PAID);
        OrderItem item = createTestOrderItem(order, product, 2, BigDecimal.valueOf(499.99));
        item.setProduct(product);
        order.setOrderItems(Set.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(1L));

        assertEquals("Order cannot be canceled", ex.getMessage());

        verify(orderRepository).findById(1L);
    }

    @Test
    void cancelOrder_orderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () ->  orderService.cancelOrder(1L));

        assertEquals("No order found", ex.getMessage());

        verify(orderRepository).findById(1L);
    }

    @Test
    void cancelOrder_doesNotUpdateStock() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.CREATED);
        OrderItem item = createTestOrderItem(order, product, 2, BigDecimal.valueOf(499.99));
        item.setProduct(product);
        order.setOrderItems(Set.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());
        assertEquals(100, product.getStockQuantity());

        verify(orderRepository).findById(1L);
    }
}
