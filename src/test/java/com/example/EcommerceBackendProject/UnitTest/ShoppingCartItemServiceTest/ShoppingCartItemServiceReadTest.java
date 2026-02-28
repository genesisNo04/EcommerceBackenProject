package com.example.EcommerceBackendProject.UnitTest.ShoppingCartItemServiceTest;

import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.createTestProduct;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ShoppingCartItemServiceReadTest extends BaseShoppingCartItemServiceTest {

    @Test
    void findItemsByUser() {
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
        List<ShoppingCartItem> items = List.of(item, item1);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ShoppingCartItem> itemsPage = new PageImpl<>(items, pageable, items.size());

        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);
        when(shoppingCartItemRepository.findByShoppingCartId(cart.getId(), pageable)).thenReturn(itemsPage);

        Page<ShoppingCartItem> returnedItems = shoppingCartItemService.findItemsByUser(1L, pageable);

        assertEquals(2, returnedItems.getTotalElements());
        assertEquals(2, returnedItems.getContent().size());
        assertEquals(items, returnedItems.getContent());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(shoppingCartItemRepository).findByShoppingCartId(cart.getId(), pageable);
    }

    @Test
    void findItemsByUser_noCartFound() {
        Pageable pageable = PageRequest.of(0, 10);

        when(shoppingCartService.getCartOrThrow(1L)).thenThrow(new NoResourceFoundException("No cart found"));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.findItemsByUser(1L, pageable));

        assertEquals("No cart found", ex.getMessage());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(shoppingCartItemRepository, never()).findByShoppingCartId(1L, pageable);
    }

    @Test
    void findItemByUserAndProduct() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 5, "testurl");
        product.setId(1L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        ShoppingCartItem item = new ShoppingCartItem(product, 2, BigDecimal.valueOf(499.99), cart);
        cart.addItem(item);

        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);
        when(shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), 1L, 1L)).thenReturn(Optional.of(item));

        ShoppingCartItem returnedItem = shoppingCartItemService.findItemByUserAndProduct(1L, 1L);

        assertSame(product, returnedItem.getProduct());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(shoppingCartItemRepository).findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), 1L, 1L);
    }

    @Test
    void findItemByUserAndProduct_noCartFound() {
        when(shoppingCartService.getCartOrThrow(1L)).thenThrow(new NoResourceFoundException("No cart found"));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.findItemByUserAndProduct(1L, 1L));

        assertEquals("No cart found", ex.getMessage());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(shoppingCartItemRepository, never()).findByShoppingCartIdAndProductIdAndShoppingCartUserId(1L, 1L, 1L);
    }

    @Test
    void findItemByUserAndProduct_noItemFound() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 5, "testurl");
        product.setId(1L);

        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        ShoppingCartItem item = new ShoppingCartItem(product, 2, BigDecimal.valueOf(499.99), cart);
        cart.addItem(item);

        when(shoppingCartService.getCartOrThrow(1L)).thenReturn(cart);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> shoppingCartItemService.findItemByUserAndProduct(1L, 1L));

        assertEquals("No item found", ex.getMessage());

        verify(shoppingCartService).getCartOrThrow(1L);
        verify(shoppingCartItemRepository).findByShoppingCartIdAndProductIdAndShoppingCartUserId(1L, 1L, 1L);
    }
}
