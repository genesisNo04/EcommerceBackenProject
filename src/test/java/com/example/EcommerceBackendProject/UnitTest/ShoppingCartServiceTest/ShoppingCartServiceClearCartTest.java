package com.example.EcommerceBackendProject.UnitTest.ShoppingCartServiceTest;

import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.createTestProduct;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartServiceClearCartTest extends BaseShoppingCartServiceTest {

    @Test
    void clearShoppingCart() {
        User user = new User();
        user.setId(1L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        cart.setUser(user);
        user.setCart(cart);

        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 5, "testurl");
        product.setId(1L);

        Product product1 = createTestProduct("XBOX", "Xbox", BigDecimal.valueOf(499.99), 5, "testurl");
        product1.setId(2L);

        cart.setId(1L);
        ShoppingCartItem item = new ShoppingCartItem(product, 2, BigDecimal.valueOf(499.99), cart);
        ShoppingCartItem item1 = new ShoppingCartItem(product1, 2, BigDecimal.valueOf(499.99), cart);
        cart.addItem(item);
        cart.addItem(item1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        shoppingCartService.clearShoppingCart(1L);

        assertTrue(cart.getItems().isEmpty());

        verify(userRepository).findById(1L);
        verify(shoppingCartRepository).findByUserId(1L);
        verifyNoMoreInteractions(userRepository, shoppingCartRepository);
    }

    @Test
    void clearShoppingCart_clearEmptyCart() {
        User user = new User();
        user.setId(1L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        cart.setUser(user);
        user.setCart(cart);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        shoppingCartService.clearShoppingCart(1L);

        assertTrue(cart.getItems().isEmpty());

        verify(userRepository).findById(1L);
        verify(shoppingCartRepository).findByUserId(1L);
        verifyNoMoreInteractions(userRepository, shoppingCartRepository);
    }

    @Test
    void clearShoppingCart_noUserFound() {

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartService.clearShoppingCart(1L));

        assertEquals("No user found", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(shoppingCartRepository, never()).findByUserId(1L);
    }

    @Test
    void clearShoppingCart_cartNotFound() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartService.clearShoppingCart(1L));

        assertEquals("No cart found", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(shoppingCartRepository).findByUserId(1L);
    }
}
