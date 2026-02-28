package com.example.EcommerceBackendProject.UnitTest.OrderServiceTest;

import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.UserTestUtils.createTestUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceReadTest extends BaseOrderServiceTest {

    @Test
    void findOrder_byId() {
        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        Order result = orderService.findOrderById(1L, 1L);

        assertSame(order, result);
        verify(orderRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    void findOrder_orderNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> orderService.findOrderById(1L, 1L));

        assertSame("Order not found", ex.getMessage());
        verify(orderRepository).findByIdAndUserId(1L, 1L);
    }

    @Test
    void findAllOrders() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        Order order3 = new Order();
        order3.setId(3L);
        Order order4 = new Order();
        order4.setId(4L);
        List<Order> orders = List.of(order1, order2, order3, order4);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> result = new PageImpl<>(orders, pageable, orders.size());

        when(orderRepository.findAll(pageable)).thenReturn(result);

        Page<Order> searchResult = orderService.findAllOrders(pageable);

        assertSame(result, searchResult);
        assertEquals(4, searchResult.getTotalElements());

        verify(orderRepository).findAll(pageable);
    }

    @Test
    void findOrders_byStatus() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        Order order3 = new Order();
        order3.setId(3L);
        Order order4 = new Order();
        order4.setId(4L);
        List<Order> orders = List.of(order1, order2, order3, order4);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> result = new PageImpl<>(orders, pageable, orders.size());

        when(orderRepository.findByUserIdAndOrderStatus(1L, OrderStatus.CREATED, pageable)).thenReturn(result);

        Page<Order> searchResult = orderService.findUserOrders(1L, OrderStatus.CREATED, null, null, pageable);

        assertSame(result, searchResult);
        assertEquals(4, searchResult.getTotalElements());

        verify(orderRepository, never()).findByUserIdAndOrderStatusAndCreatedAtBetween(1L, OrderStatus.CREATED, null, null, pageable);
        verify(orderRepository, never()).findByUserIdAndCreatedAtBetween(1L, null, null, pageable);
        verify(orderRepository).findByUserIdAndOrderStatus(1L, OrderStatus.CREATED, pageable);
        verify(orderRepository, never()).findByUserId(1L, pageable);
    }

    @Test
    void findOrders_byDateRange() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        Order order3 = new Order();
        order3.setId(3L);
        Order order4 = new Order();
        order4.setId(4L);
        List<Order> orders = List.of(order1, order2, order3, order4);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> result = new PageImpl<>(orders, pageable, orders.size());
        LocalDateTime start = LocalDateTime.of(2026, 02, 02, 16, 30);
        LocalDateTime end = LocalDateTime.of(2026, 02, 02, 16, 30);

        when(orderRepository.findByUserIdAndCreatedAtBetween(1L, start, end, pageable)).thenReturn(result);

        Page<Order> searchResult = orderService.findUserOrders(1L, null, start, end, pageable);

        assertSame(result, searchResult);
        assertEquals(4, searchResult.getTotalElements());

        verify(orderRepository, never()).findByUserIdAndOrderStatusAndCreatedAtBetween(1L, OrderStatus.CREATED, null, null, pageable);
        verify(orderRepository).findByUserIdAndCreatedAtBetween(1L, start, end, pageable);
        verify(orderRepository, never()).findByUserIdAndOrderStatus(1L, OrderStatus.CREATED, pageable);
        verify(orderRepository, never()).findByUserId(1L, pageable);
    }

    @Test
    void findOrders_byDateRangeAndStatus() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        Order order3 = new Order();
        order3.setId(3L);
        Order order4 = new Order();
        order4.setId(4L);
        List<Order> orders = List.of(order1, order2, order3, order4);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> result = new PageImpl<>(orders, pageable, orders.size());
        LocalDateTime start = LocalDateTime.of(2026, 02, 02, 16, 30);
        LocalDateTime end = LocalDateTime.of(2026, 02, 02, 16, 30);

        when(orderRepository.findByUserIdAndOrderStatusAndCreatedAtBetween(1L, OrderStatus.CREATED, start, end, pageable)).thenReturn(result);

        Page<Order> searchResult = orderService.findUserOrders(1L, OrderStatus.CREATED, start, end, pageable);

        assertSame(result, searchResult);
        assertEquals(4, searchResult.getTotalElements());

        verify(orderRepository).findByUserIdAndOrderStatusAndCreatedAtBetween(1L, OrderStatus.CREATED, start, end, pageable);
        verify(orderRepository, never()).findByUserIdAndCreatedAtBetween(1L, start, end, pageable);
        verify(orderRepository, never()).findByUserIdAndOrderStatus(1L, OrderStatus.CREATED, pageable);
        verify(orderRepository, never()).findByUserId(1L, pageable);
    }

    @Test
    void findOrders_noFilters() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);
        Order order3 = new Order();
        order3.setId(3L);
        Order order4 = new Order();
        order4.setId(4L);
        List<Order> orders = List.of(order1, order2, order3, order4);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> result = new PageImpl<>(orders, pageable, orders.size());

        when(orderRepository.findByUserId(1L, pageable)).thenReturn(result);

        Page<Order> searchResult = orderService.findUserOrders(1L, null, null, null, pageable);

        assertSame(result, searchResult);
        assertEquals(4, searchResult.getTotalElements());

        verify(orderRepository, never()).findByUserIdAndOrderStatusAndCreatedAtBetween(1L, OrderStatus.CREATED, null, null, pageable);
        verify(orderRepository, never()).findByUserIdAndCreatedAtBetween(1L, null, null, pageable);
        verify(orderRepository, never()).findByUserIdAndOrderStatus(1L, OrderStatus.CREATED, pageable);
        verify(orderRepository).findByUserId(1L, pageable);
    }

    @Test
    void findOrderAdmin_byId() {
        Order order1 = new Order();
        order1.setId(1L);
        Pageable pageable = PageRequest.of(0, 10);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order1));

        Page<Order> returnOrder = orderService.findByOrderId(1L, pageable);

        assertEquals(1, returnOrder.getTotalElements());
        assertEquals(1, returnOrder.getContent().size());
        assertSame(order1, returnOrder.getContent().get(0));
        assertEquals(pageable, returnOrder.getPageable());

        verify(orderRepository).findById(1L);
    }

    @Test
    void findOrderAdmin_noOrderFound() {
        Pageable pageable = PageRequest.of(0, 10);

        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> orderService.findByOrderId(1L, pageable));

        assertEquals("No order found", ex.getMessage());

        verify(orderRepository).findById(1L);
    }
}
