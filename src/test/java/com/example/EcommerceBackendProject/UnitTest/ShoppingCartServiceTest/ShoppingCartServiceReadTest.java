package com.example.EcommerceBackendProject.UnitTest.ShoppingCartServiceTest;

import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartServiceReadTest extends BaseShoppingCartServiceTest {

    @Test
    void findByUserId() {
        User user = new User();
        user.setId(1L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        cart.setUser(user);
        user.setCart(cart);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(shoppingCartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        ShoppingCart returnCart = shoppingCartService.findByUserId(1L);

        assertSame(user, cart.getUser());
        assertSame(cart, user.getCart());

        verify(userRepository).findById(1L);
        verify(shoppingCartRepository).findByUserId(1L);
    }

    @Test
    void findByUserId_userNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartService.findByUserId(1L));

        assertEquals("No user found", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(shoppingCartRepository, never()).findByUserId(1L);
    }

    @Test
    void findByUserId_userCartNotFound() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartService.findByUserId(1L));

        assertEquals("No cart found", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(shoppingCartRepository).findByUserId(1L);
    }

    @Test
    void getCart() {
        User user = new User();
        user.setId(1L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        cart.setUser(user);
        user.setCart(cart);

        when(shoppingCartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        ShoppingCart returnCart = shoppingCartService.getCartOrThrow(1L);

        assertSame(cart, returnCart);

        verify(shoppingCartRepository).findByUserId(1L);
    }

    @Test
    void getCart_cartNoFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartService.getCartOrThrow(1L));

        assertEquals("No cart found", ex.getMessage());

        verify(shoppingCartRepository).findByUserId(1L);
    }
}
