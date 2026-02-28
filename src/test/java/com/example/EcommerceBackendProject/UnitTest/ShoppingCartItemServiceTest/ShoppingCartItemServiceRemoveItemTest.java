package com.example.EcommerceBackendProject.UnitTest.ShoppingCartItemServiceTest;

import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.createTestProduct;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartItemServiceRemoveItemTest extends BaseShoppingCartItemServiceTest {

    @Test
    void removeItemFromCart() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 5, "testurl");
        product.setId(1L);

        Product product1 = createTestProduct("XBOX", "Xbox", BigDecimal.valueOf(499.99), 5, "testurl");
        product1.setId(2L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        ShoppingCartItem item = new ShoppingCartItem(product, 2, BigDecimal.valueOf(499.99), cart);
        ShoppingCartItem item1 = new ShoppingCartItem(product1, 2, BigDecimal.valueOf(499.99), cart);
        cart.addItem(item);
        cart.addItem(item1);

        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);
        when(shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), 1L, 1L)).thenReturn(Optional.of(item));

        shoppingCartItemService.removeItemFromCart(1L, 1L);

        assertEquals(1, cart.getItems().size());
        assertTrue(cart.getItems().contains(item1));
        assertFalse(cart.getItems().contains(item));

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(shoppingCartItemRepository).findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), 1L, 1L);
    }

    @Test
    void removeItemFromCart_cartNotFound() {
        when(shoppingCartService.getCartOrThrow(1L)).thenThrow(new NoResourceFoundException("No cart found"));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.removeItemFromCart(1L, 1L));

        assertEquals("No cart found", ex.getMessage());

        verify(shoppingCartService).getCartOrThrow(1L);
        verifyNoInteractions(shoppingCartItemRepository);
    }

    @Test
    void removeItemFromCart_itemNotFound() {
        ShoppingCart cart = new ShoppingCart();
        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.removeItemFromCart(1L, 1L));

        assertEquals("No item found", ex.getMessage());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(shoppingCartItemRepository).findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), 1L, 1L);
    }
}
