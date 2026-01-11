package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.Entity.ShoppingCart;

public interface ShoppingCartService {

    ShoppingCart findByUserId(Long userId);

    void clearShoppingCart(Long userId);
}
