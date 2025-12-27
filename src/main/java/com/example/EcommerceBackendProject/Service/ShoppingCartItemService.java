package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ShoppingCartItemService {

    ShoppingCartItem addItemToCart(Long productId, int quantity, Long userId);

    ShoppingCartItem updateItemQuantity(Long cartId, Long productId, int quantity, Long userId);

    void removeItemFromCart(Long cartId, Long productId, Long userId);

    Page<ShoppingCartItem> findByShoppingCartId(Long shoppingCartId, Pageable pageable);

    Optional<ShoppingCartItem> findByShoppingCartIdAndProductIdAndUserId(Long cartId, Long productId, Long userId);

    void clearShoppingCart(Long shoppingCartId, Long userId);

    void deleteItemsByProduct(Long productId);
}
