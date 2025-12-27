package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.NoUserFoundException;
import com.example.EcommerceBackendProject.Repository.ShoppingCartRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.ShoppingCartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public ShoppingCart createShoppingCart(Long userId) {
        Optional<ShoppingCart> existingCart = shoppingCartRepository.findByUserId(userId);
        if (existingCart.isPresent()) {
            return existingCart.get();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found"));

        ShoppingCart newCart = new ShoppingCart(user);

        return shoppingCartRepository.save(newCart);
    }

    @Override
    public Optional<ShoppingCart> findByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found"));
        return shoppingCartRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void clearShoppingCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found"));

        ShoppingCart cart = findByUserId(userId)
                .orElseThrow(() -> new NoResourceFoundException("Shopping cart not found"));
        cart.getItems().clear();
    }
}
