package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Repository.ShoppingCartItemRepository;
import com.example.EcommerceBackendProject.Repository.ShoppingCartRepository;
import com.example.EcommerceBackendProject.Service.ShoppingCartItemService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShoppingCartItemServiceImpl implements ShoppingCartItemService {

    @Autowired
    private ShoppingCartItemRepository shoppingCartItemRepository;

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public ShoppingCartItem addItemToCart(Long cartId, Long productId, int quantity, Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new NoResourceFoundException("No cart found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found"));

        ShoppingCartItem shoppingCartItem = new ShoppingCartItem(product, quantity, product.getPrice(), cart);

        cart.getItems().add(shoppingCartItem);

        return shoppingCartItem;
    }

    @Override
    @Transactional
    public ShoppingCartItem updateItemQuantity(Long cartId, Long productId, int quantity, Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new NoResourceFoundException("No cart found"));

        ShoppingCartItem item = shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(cartId, productId, userId)
                .orElseThrow(() -> new NoResourceFoundException("No item found"));;
        item.setQuantity(quantity);
        return item;
    }

    @Override
    public void removeItemFromCart(Long cartItemId, Long userId) {

    }

    @Override
    public Page<ShoppingCartItem> findByShoppingCartId(Long shoppingCartId, Pageable pageable) {
        return null;
    }

    @Override
    public Optional<ShoppingCartItem> findByShoppingCartIdAndProductId(Long cartId, Long productId) {
        return Optional.empty();
    }

    @Override
    public void clearShoppingCart(Long shoppingCartId, Long userId) {

    }
}
