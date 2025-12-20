package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.Entity.ShoppingCart;

import java.util.Optional;

public interface ShoppingCartService {

    ShoppingCart createShoppingCart(Long userId);

    Optional<ShoppingCart> findByUserId(Long userId);

    void clearShoppingCart(Long userId);
}
