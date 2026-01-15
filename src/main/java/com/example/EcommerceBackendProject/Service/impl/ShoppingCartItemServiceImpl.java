package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.ShoppingCartItemUpdateRequestDTO;
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

    private ShoppingCart getCartOrThrow(Long userId) {
        return shoppingCartRepository.findByUserId(userId)
                .orElseThrow(() -> new NoResourceFoundException("No cart found"));
    }

    @Override
    @Transactional
    public ShoppingCartItem addItemToCart(ShoppingCartItemRequestDTO shoppingCartItemRequestDTO, Long userId) {
        if (shoppingCartItemRequestDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        ShoppingCart cart = getCartOrThrow(userId);

        Product product = productRepository.findById(shoppingCartItemRequestDTO.getProductId())
                .orElseThrow(() -> new NoResourceFoundException("No product found"));

        if (shoppingCartItemRequestDTO.getQuantity() > product.getStockQuantity()) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        Optional<ShoppingCartItem> existingItem = shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(
                cart.getId(), shoppingCartItemRequestDTO.getProductId(), userId
        );

        if (existingItem.isPresent()) {
            int newQuantity = existingItem.get().getQuantity() + shoppingCartItemRequestDTO.getQuantity();
            if (newQuantity > product.getStockQuantity()) {
                throw new IllegalArgumentException("Insufficient stock");
            }
            existingItem.get().setQuantity(newQuantity);
            return shoppingCartItemRepository.save(existingItem.get());
        }

        ShoppingCartItem shoppingCartItem = new ShoppingCartItem(product, shoppingCartItemRequestDTO.getQuantity(), product.getPrice(), cart);
        cart.addItem(shoppingCartItem);
        return shoppingCartItemRepository.save(shoppingCartItem);
    }

    @Override
    @Transactional
    public ShoppingCartItem updateItemQuantity(ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO, Long userId, Long productId) {
        if (shoppingCartItemUpdateRequestDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        ShoppingCart cart = getCartOrThrow(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found"));

        if (shoppingCartItemUpdateRequestDTO.getQuantity() > product.getStockQuantity()) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        ShoppingCartItem item = shoppingCartItemRepository
                .findByShoppingCartIdAndProductIdAndShoppingCartUserId(
                        cart.getId(), productId, userId
                ).orElseThrow(() -> new NoResourceFoundException("No resource found"));


        item.setQuantity(shoppingCartItemUpdateRequestDTO.getQuantity());
        return item;
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long productId, Long userId) {
        ShoppingCart cart = getCartOrThrow(userId);

        ShoppingCartItem item = shoppingCartItemRepository
                .findByShoppingCartIdAndProductIdAndShoppingCartUserId(
                        cart.getId(), productId, userId
                ).orElseThrow(() -> new NoResourceFoundException("No resource found"));

        cart.removeItem(item);
    }

    @Override
    public Page<ShoppingCartItem> findItemsByUser(Long userId, Pageable pageable) {
        ShoppingCart cart = getCartOrThrow(userId);
        return shoppingCartItemRepository.findByShoppingCartId(cart.getId(), pageable);
    }

    @Override
    public ShoppingCartItem findItemByUserAndProduct(Long productId, Long userId) {
        ShoppingCart cart = getCartOrThrow(userId);
        return shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), productId, userId)
                .orElseThrow(() -> new NoResourceFoundException("No item founds"));
    }

}
