package com.example.EcommerceBackendProject.UnitTest.OrderServiceTest;
import com.example.EcommerceBackendProject.Entity.*;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.OrderItemsTestUtils.*;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.*;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.UserTestUtils.createTestUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceCheckoutTest extends BaseOrderServiceTest {

    @Test
    void checkout() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);
        Product product1 = createTestProduct("XBOX", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product1.setId(2L);

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderStatus(OrderStatus.CREATED);
        OrderItem item = createTestOrderItem(order, product, 2, BigDecimal.valueOf(499.99));
        item.setProduct(product);
        order.setOrderItems(Set.of(item));

        ShoppingCart shoppingCart = new ShoppingCart();
        ShoppingCartItem shoppingCartItem = new ShoppingCartItem(product, 2, BigDecimal.valueOf(499.99), shoppingCart);
        ShoppingCartItem shoppingCartItem1 = new ShoppingCartItem(product1, 2, BigDecimal.valueOf(499.99), shoppingCart);
        shoppingCart.addItem(shoppingCartItem);
        shoppingCart.addItem(shoppingCartItem1);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(shoppingCart);

        orderService.checkout(1L);

        assertEquals(OrderStatus.PENDING_PAYMENT, order.getOrderStatus());
        assertEquals(BigDecimal.valueOf(999.98), order.getTotalAmount());
        assertEquals(98, product.getStockQuantity());

        verify(orderRepository).findById(1L);
        verify(shoppingCartService).getCartOrThrow(1L);
    }

    @Test
    void checkout_noOrderFound() {

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> orderService.checkout(1L));

        assertEquals("Order not found", ex.getMessage());

        verify(orderRepository).findById(1L);
        verify(shoppingCartService, never()).getCartOrThrow(1L);
    }

    @Test
    void checkout_orderAlreadyCheckout() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);
        Product product1 = createTestProduct("XBOX", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product1.setId(2L);

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PAID);
        OrderItem item = createTestOrderItem(order, product, 2, BigDecimal.valueOf(499.99));
        item.setProduct(product);
        order.setOrderItems(Set.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.checkout(1L));

        assertEquals("Order cannot be checked out", ex.getMessage());

        verify(orderRepository).findById(1L);
        verify(shoppingCartService, never()).getCartOrThrow(1L);
    }

    @Test
    void checkout_checkoutMultipleTime() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);
        Product product1 = createTestProduct("XBOX", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product1.setId(2L);

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);
        OrderItem item = createTestOrderItem(order, product, 2, BigDecimal.valueOf(499.99));
        item.setProduct(product);
        order.setOrderItems(Set.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.checkout(1L);

        assertEquals(OrderStatus.PENDING_PAYMENT, order.getOrderStatus());

        verify(orderRepository).findById(1L);
        verify(shoppingCartService, never()).getCartOrThrow(1L);
    }

    @Test
    void checkout_cartNotFound() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());
        user.setId(1L);
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);
        Product product1 = createTestProduct("XBOX", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product1.setId(2L);

        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderStatus(OrderStatus.CREATED);
        OrderItem item = createTestOrderItem(order, product, 2, BigDecimal.valueOf(499.99));
        item.setProduct(product);
        order.setOrderItems(Set.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(shoppingCartService.getCartOrThrow(1L)).thenThrow(new NoResourceFoundException("No cart found"));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> orderService.checkout(1L));

        assertEquals("No cart found", ex.getMessage());

        verify(orderRepository).findById(1L);
        verify(shoppingCartService).getCartOrThrow(1L);
    }
}
