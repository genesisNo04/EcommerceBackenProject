package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ShoppingCartItemService {

    ShoppingCartItem addItemToCart(Long cartId, Long productId, int quantity, Long userId);

    ShoppingCartItem updateItemQuantity(Long cartId, Long productId, int quantity, Long userId);

    void removeItemFromCart(Long cartItemId, Long userId);

    Page<ShoppingCartItem> findByShoppingCartId(Long shoppingCartId, Pageable pageable);

    Optional<ShoppingCartItem> findByShoppingCartIdAndProductId(Long cartId, Long productId);

    void clearShoppingCart(Long shoppingCartId, Long userId);
}
