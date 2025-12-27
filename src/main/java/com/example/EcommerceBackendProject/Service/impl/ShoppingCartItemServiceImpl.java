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
import com.example.EcommerceBackendProject.Exception.AccessDeniedException;

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
    public ShoppingCartItem addItemToCart(Long productId, int quantity, Long userId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new NoResourceFoundException("No cart found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found"));

        Optional<ShoppingCartItem> existingItem = shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(
                cart.getId(), productId, userId
        );

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
            return existingItem.get();
        }

        ShoppingCartItem shoppingCartItem = new ShoppingCartItem(product, quantity, product.getPrice(), cart);
        cart.addItem(shoppingCartItem);
        return shoppingCartItem;
    }

    @Override
    @Transactional
    public ShoppingCartItem updateItemQuantity(Long cartId, Long productId, int quantity, Long userId) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new NoResourceFoundException("No cart found"));

        ShoppingCartItem item = shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(cartId, productId, userId)
                .orElseThrow(() -> new NoResourceFoundException("No item found"));

        if (!item.getShoppingCart().getId().equals(cart.getId())) {
            throw new AccessDeniedException("Item does not belong to this cart");
        }

        item.setQuantity(quantity);
        return item;
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long cartId, Long productId, Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new NoResourceFoundException("No cart found"));

        ShoppingCartItem item = shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(cartId, productId, userId)
                .orElseThrow(() -> new NoResourceFoundException("No item found"));

        if (!item.getShoppingCart().getId().equals(cart.getId())) {
            throw new AccessDeniedException("Item does not belong to this cart");
        }

        cart.removeItem(item);
    }

    @Override
    public Page<ShoppingCartItem> findByShoppingCartId(Long shoppingCartId, Pageable pageable) {
        return shoppingCartItemRepository.findByShoppingCartId(shoppingCartId, pageable);
    }

    @Override
    public Optional<ShoppingCartItem> findByShoppingCartIdAndProductIdAndUserId(Long cartId, Long productId, Long userId) {
        return shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(cartId, productId, userId);
    }

    @Override
    @Transactional
    public void clearShoppingCart(Long shoppingCartId, Long userId) {
        ShoppingCart cart = shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new NoResourceFoundException("No cart found"));

        cart.clearItems();
    }

    @Override
    public void deleteItemsByProduct(Long productId) {
        shoppingCartItemRepository.deleteByProductId(productId);
    }
}
