package com.example.EcommerceBackendProject.UnitTest.OrderServiceTest;

import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.OrderItem;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.OrderItemsTestUtils.createOrderItemDto;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.OrderItemsTestUtils.createTestOrderItem;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.createTestProduct;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.UserTestUtils.createTestUser;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceCreateTest extends BaseOrderServiceTest {

    @Test
    void createOrder() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);
        Product product1 = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 100, "testurl");
        product1.setId(2L);
        OrderItem orderItem = createTestOrderItem(null, product, 1, BigDecimal.valueOf(499.99));
        OrderItem orderItem1 = createTestOrderItem(null, product1, 1, BigDecimal.valueOf(499.99));
        Set<OrderItem> items = Set.of(orderItem, orderItem1);
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO(List.of(createOrderItemDto(1L, 1), createOrderItemDto(2L, 1)));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product1));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order order = orderService.createOrder(orderRequestDTO, 1L);

        assertEquals(OrderStatus.CREATED, order.getOrderStatus());
        assertEquals(BigDecimal.valueOf(999.98), order.getTotalAmount());
        assertTrue(order.getOrderItems()
                .stream()
                .anyMatch(item -> item.getProduct().getId().equals(1L)
                                            && item.getProduct().getProductName().equals("PS5")
                                            && item.getQuantity() == 1
                                            && item.getPriceAtPurchase().equals(BigDecimal.valueOf(499.99))));
        assertTrue(order.getOrderItems()
                .stream()
                .anyMatch(item -> item.getProduct().getId().equals(2L)
                        && item.getProduct().getProductName().equals("XBOX")
                        && item.getQuantity() == 1
                        && item.getPriceAtPurchase().equals(BigDecimal.valueOf(499.99))));

        verify(userRepository).findById(1L);
        verify(productRepository).findById(1L);
        verify(productRepository).findById(2L);
        verify(orderRepository).save(any(Order.class));
    }

}
