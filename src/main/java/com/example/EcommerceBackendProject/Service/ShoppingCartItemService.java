package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.ShoppingCartItemUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShoppingCartItemService {

    ShoppingCartItem addItemToCart(ShoppingCartItemRequestDTO shoppingCartItemRequestDTO, Long userId);

    ShoppingCartItem updateItemQuantity(ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO, Long userId, Long productId);

    void removeItemFromCart(Long productId, Long userId);

    Page<ShoppingCartItem> findItemsByUser(Long userId, Pageable pageable);

    ShoppingCartItem findItemByUserAndProduct(Long productId, Long userId);
}
