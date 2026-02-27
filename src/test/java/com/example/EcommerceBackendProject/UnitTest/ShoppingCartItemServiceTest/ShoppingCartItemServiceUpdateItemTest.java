package com.example.EcommerceBackendProject.UnitTest.ShoppingCartItemServiceTest;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.ShoppingCartItemUpdateRequestDTO;
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

public class ShoppingCartItemServiceUpdateItemTest extends BaseShoppingCartItemTest {

    @Test
    void updateItemQuantity() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = new ShoppingCartItemUpdateRequestDTO(3);
        ShoppingCartItem item = new ShoppingCartItem(product, 2, BigDecimal.valueOf(499.99), cart);
        cart.addItem(item);

        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), 1L, 1L)).thenReturn(Optional.of(item));

        ShoppingCartItem returnItem = shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, 1L, 1L);

        assertEquals(3, returnItem.getQuantity());
        assertEquals(cart.getId(), returnItem.getShoppingCart().getId());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(productRepository).findById(1L);
        verify(shoppingCartItemRepository).findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), 1L, 1L);
    }

    @Test
    void updateItemQuantity_updateSameQuantity() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = new ShoppingCartItemUpdateRequestDTO(2);
        ShoppingCartItem item = new ShoppingCartItem(product, 2, BigDecimal.valueOf(499.99), cart);
        cart.addItem(item);

        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), 1L, 1L)).thenReturn(Optional.of(item));

        ShoppingCartItem returnItem = shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, 1L, 1L);

        assertEquals(2, returnItem.getQuantity());
        assertEquals(cart.getId(), returnItem.getShoppingCart().getId());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(productRepository).findById(1L);
        verify(shoppingCartItemRepository).findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), 1L, 1L);
    }

    @Test
    void updateItemQuantity_invalidQuantity() {
        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = new ShoppingCartItemUpdateRequestDTO(-1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, 1L, 1L));

        assertEquals("Quantity must be greater than zero", ex.getMessage());

        verifyNoInteractions(shoppingCartService, productRepository, shoppingCartItemRepository);
    }

    @Test
    void updateItemQuantity_updateZeroQuantity() {
        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = new ShoppingCartItemUpdateRequestDTO(0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, 1L, 1L));

        assertEquals("Quantity must be greater than zero", ex.getMessage());

        verifyNoInteractions(shoppingCartService, productRepository, shoppingCartItemRepository);
    }

    @Test
    void updateItemQuantity_noCartFound() {
        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = new ShoppingCartItemUpdateRequestDTO(3);

        when(shoppingCartService.getCartOrThrow(1L)).thenThrow(new NoResourceFoundException("No cart found"));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, 1L, 1L));

        assertEquals("No cart found", ex.getMessage());

        verify(shoppingCartService).getCartOrThrow(1L);
        verifyNoInteractions(productRepository, shoppingCartItemRepository);
    }

    @Test
    void updateItemQuantity_noProductFound() {
        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = new ShoppingCartItemUpdateRequestDTO(3);
        ShoppingCart cart = new ShoppingCart();

        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, 1L, 1L));

        assertEquals("No product found", ex.getMessage());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(productRepository).findById(1L);
        verifyNoInteractions(shoppingCartItemRepository);
    }

    @Test
    void updateItemQuantity_insufficientStock() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 5, "testurl");
        product.setId(1L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = new ShoppingCartItemUpdateRequestDTO(6);
        ShoppingCartItem item = new ShoppingCartItem(product, 2, BigDecimal.valueOf(499.99), cart);
        cart.addItem(item);

        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, 1L, 1L));

        assertEquals("Insufficient stock", ex.getMessage());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(productRepository).findById(1L);
        verify(shoppingCartItemRepository, never()).findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), 1L, 1L);
    }

    @Test
    void updateItemQuantity_noItemFound() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 5, "testurl");
        product.setId(1L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO = new ShoppingCartItemUpdateRequestDTO(2);

        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), 1L, 1L)).thenReturn(Optional.empty());

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.updateItemQuantity(shoppingCartItemUpdateRequestDTO, 1L, 1L));

        assertEquals("No item found", ex.getMessage());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(productRepository).findById(1L);
        verify(shoppingCartItemRepository).findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), 1L, 1L);
    }
}
