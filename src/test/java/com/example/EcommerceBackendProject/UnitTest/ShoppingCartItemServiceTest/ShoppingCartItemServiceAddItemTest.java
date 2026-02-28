package com.example.EcommerceBackendProject.UnitTest.ShoppingCartItemServiceTest;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemRequestDTO;
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

public class ShoppingCartItemServiceAddItemTest extends BaseShoppingCartItemServiceTest {

    @Test
    void addItemToCart_itemExistInCart() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = new ShoppingCartItemRequestDTO(1L, 2);
        ShoppingCartItem item = new ShoppingCartItem(product, 2, BigDecimal.valueOf(499.99), cart);
        cart.addItem(item);

        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), shoppingCartItemRequestDTO.getProductId(), 1L)).thenReturn(Optional.of(item));
        when(shoppingCartItemRepository.save(any(ShoppingCartItem.class))).thenAnswer(inv -> inv.getArgument(0));

        ShoppingCartItem returnItem = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, 1L);

        assertEquals(4, returnItem.getQuantity());
        assertEquals(cart.getId(), returnItem.getShoppingCart().getId());
        assertTrue(cart.getItems().contains(returnItem));

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(productRepository).findById(1L);
        verify(shoppingCartItemRepository).findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), shoppingCartItemRequestDTO.getProductId(), 1L);
        verify(shoppingCartItemRepository).save(any(ShoppingCartItem.class));
    }

    @Test
    void addItemToCart_itemNotExistInCart() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setId(1L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = new ShoppingCartItemRequestDTO(1L, 2);
        ShoppingCartItem item = new ShoppingCartItem(product, 2, BigDecimal.valueOf(499.99), cart);

        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), shoppingCartItemRequestDTO.getProductId(), 1L)).thenReturn(Optional.empty());
        when(shoppingCartItemRepository.save(any(ShoppingCartItem.class))).thenAnswer(inv -> inv.getArgument(0));

        ShoppingCartItem returnItem = shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, 1L);

        assertEquals(2, returnItem.getQuantity());
        assertEquals(cart.getId(), returnItem.getShoppingCart().getId());
        assertEquals(product.getPrice(), returnItem.getPriceSnapshot());
        assertTrue(cart.getItems().contains(returnItem));

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(productRepository).findById(1L);
        verify(shoppingCartItemRepository).findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), shoppingCartItemRequestDTO.getProductId(), 1L);
        verify(shoppingCartItemRepository).save(any(ShoppingCartItem.class));
    }

    @Test
    void addItemToCart_invalidQuantity() {
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = new ShoppingCartItemRequestDTO(1L, -5);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, 1L));

        assertEquals("Quantity must be greater than zero", ex.getMessage());

        verifyNoInteractions(shoppingCartService, productRepository, shoppingCartItemRepository);
    }

    @Test
    void addItemToCart_cartNotFound() {
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = new ShoppingCartItemRequestDTO(1L, 5);

        when(shoppingCartService.getCartOrThrow(1L)).thenThrow(new NoResourceFoundException("No cart found"));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, 1L));

        assertEquals("No cart found", ex.getMessage());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(productRepository, never()).findById(1L);
        verify(shoppingCartItemRepository, never()).findByShoppingCartIdAndProductIdAndShoppingCartUserId(1L, shoppingCartItemRequestDTO.getProductId(), 1L);
        verify(shoppingCartItemRepository, never()).save(any(ShoppingCartItem.class));
    }

    @Test
    void addItemToCart_productNotFound() {
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = new ShoppingCartItemRequestDTO(1L, 5);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, 1L));

        assertEquals("No product found", ex.getMessage());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(productRepository).findById(1L);
        verify(shoppingCartItemRepository, never()).findByShoppingCartIdAndProductIdAndShoppingCartUserId(1L, shoppingCartItemRequestDTO.getProductId(), 1L);
        verify(shoppingCartItemRepository, never()).save(any(ShoppingCartItem.class));
    }

    @Test
    void addItemToCart_insufficientStock() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 2, "testurl");
        product.setId(1L);

        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = new ShoppingCartItemRequestDTO(1L, 5);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);

        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, 1L));

        assertEquals("Insufficient stock", ex.getMessage());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(productRepository).findById(1L);
        verify(shoppingCartItemRepository, never()).findByShoppingCartIdAndProductIdAndShoppingCartUserId(1L, shoppingCartItemRequestDTO.getProductId(), 1L);
        verify(shoppingCartItemRepository, never()).save(any(ShoppingCartItem.class));
    }

    @Test
    void addItemToCart_itemExistInCart_quantityLargerThanStock() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 3, "testurl");
        product.setId(1L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        ShoppingCartItemRequestDTO shoppingCartItemRequestDTO = new ShoppingCartItemRequestDTO(1L, 2);
        ShoppingCartItem item = new ShoppingCartItem(product, 2, BigDecimal.valueOf(499.99), cart);

        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), shoppingCartItemRequestDTO.getProductId(), 1L)).thenReturn(Optional.of(item));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> shoppingCartItemService.addItemToCart(shoppingCartItemRequestDTO, 1L));

        assertEquals("Insufficient stock", ex.getMessage());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(productRepository).findById(1L);
        verify(shoppingCartItemRepository).findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), shoppingCartItemRequestDTO.getProductId(), 1L);
        verify(shoppingCartItemRepository, never()).save(any(ShoppingCartItem.class));
    }
}
