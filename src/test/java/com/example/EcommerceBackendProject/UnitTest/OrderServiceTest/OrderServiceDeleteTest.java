package com.example.EcommerceBackendProject.UnitTest.OrderServiceTest;

import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceDeleteTest extends BaseOrderServiceTest{

    @Test
    void deleteOrder() {
        Order order = new Order();
        order.setOrderStatus(OrderStatus.CREATED);
        order.setId(1L);

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        orderService.deleteOrder(1L, 1L);

        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_orderNotInCreatedStatus() {
        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.deleteOrder(1L, 1L));
        assertEquals("Only CREATED orders can be deleted", ex.getMessage());

        verify(orderRepository, never()).delete(order);
    }

    @Test
    void deleteOrder_orderNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> orderService.deleteOrder(1L, 1L));
        assertEquals("Order not found", ex.getMessage());

        verify(orderRepository, never()).delete(any(Order.class));
    }
}
