package com.example.EcommerceBackendProject.UnitTest.OrderItemServiceTest;

import com.example.EcommerceBackendProject.Entity.OrderItem;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderItemServiceReadTest extends BaseOrderItemServiceTest{

    @Test
    void findOrdersItem_byOrderId() {
        OrderItem item1 = new OrderItem();
        OrderItem item2 = new OrderItem();
        OrderItem item3 = new OrderItem();
        List<OrderItem> items = List.of(item1, item2, item3);
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderItem> result = new PageImpl<>(items, pageable, items.size());

        when(orderItemRepository.findByOrderId(1L, pageable)).thenReturn(result);

        Page<OrderItem> response = orderItemService.findOrderItems(1L, pageable);

        assertEquals(items, response.getContent());
        assertEquals(3, response.getTotalElements());

        verify(orderItemRepository).findByOrderId(1L, pageable);
    }

    @Test
    void findOrdersItem_byUserId() {
        OrderItem item1 = new OrderItem();
        OrderItem item2 = new OrderItem();
        OrderItem item3 = new OrderItem();
        List<OrderItem> items = List.of(item1, item2, item3);
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderItem> result = new PageImpl<>(items, pageable, items.size());

        when(orderItemRepository.findByOrderUserId(1L, pageable)).thenReturn(result);

        Page<OrderItem> response = orderItemService.findAllOrderItemsForUser(1L, pageable);

        assertEquals(items, response.getContent());
        assertEquals(3, response.getTotalElements());

        verify(orderItemRepository).findByOrderUserId(1L, pageable);
    }

    @Test
    void findOrdersItem_byUserIdAndOrderId() {
        OrderItem item1 = new OrderItem();
        OrderItem item2 = new OrderItem();
        OrderItem item3 = new OrderItem();
        List<OrderItem> items = List.of(item1, item2, item3);
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderItem> result = new PageImpl<>(items, pageable, items.size());

        when(orderItemRepository.findByOrderIdAndOrderUserId(1L, 1L, pageable)).thenReturn(result);

        Page<OrderItem> response = orderItemService.findOrderItemsForUserInOrder(1L, 1L, pageable);

        assertEquals(items, response.getContent());
        assertEquals(3, response.getTotalElements());

        verify(orderItemRepository).findByOrderIdAndOrderUserId(1L, 1L, pageable);
    }

    @Test
    void findOrdersItem_emptyResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<OrderItem> emptyPage = Page.empty(pageable);

        when(orderItemRepository.findByOrderId(1L, pageable)).thenReturn(emptyPage);

        Page<OrderItem> response =
                orderItemService.findOrderItems(1L, pageable);

        assertTrue(response.isEmpty());
        assertEquals(0, response.getTotalElements());

        verify(orderItemRepository).findByOrderId(1L, pageable);
    }
}
