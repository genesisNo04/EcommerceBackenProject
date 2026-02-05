package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.NoUserFoundException;
import com.example.EcommerceBackendProject.Repository.ShoppingCartRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.ShoppingCartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
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

    private static final Logger log = LoggerFactory.getLogger(ShoppingCartServiceImpl.class);

    @Override
    public ShoppingCart findByUserId(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found"));
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId).orElseThrow(() -> new NoResourceFoundException("Shopping cart not found!"));;
        log.info("FETCHED cart [cartId={}] for user [userId={}]", cart.getId(), userId);
        return cart;
    }

    @Override
    @Transactional
    public void clearShoppingCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found"));

        ShoppingCart cart = findByUserId(userId);
        cart.getItems().clear();

        log.info("CLEARED cart [cartId={}] for user [userId={}]", cart.getId(), userId);
    }

    @Override
    public ShoppingCart getCartOrThrow(Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new NoResourceFoundException("No cart found"));

        log.info("FETCHED cart [cartId={}] for user [userId={}]", cart.getId(), userId);
        return cart;
    }
}
