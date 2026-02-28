package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.ShoppingCartItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.ShoppingCartItemUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Entity.ShoppingCart;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Repository.ShoppingCartItemRepository;
import com.example.EcommerceBackendProject.Service.ShoppingCartItemService;
import com.example.EcommerceBackendProject.Service.ShoppingCartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ShoppingCartItemServiceImpl implements ShoppingCartItemService {

    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final ShoppingCartService shoppingCartService;
    private final ProductRepository productRepository;
    private final static Logger log = LoggerFactory.getLogger(ShoppingCartItemServiceImpl.class);

    public ShoppingCartItemServiceImpl(ShoppingCartItemRepository shoppingCartItemRepository, ShoppingCartService shoppingCartService, ProductRepository productRepository) {
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.shoppingCartService = shoppingCartService;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public ShoppingCartItem addItemToCart(ShoppingCartItemRequestDTO shoppingCartItemRequestDTO, Long userId) {
        if (shoppingCartItemRequestDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        ShoppingCart cart = shoppingCartService.getCartOrThrow(userId);

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
        log.info("ADDED product [productId={}] [quantity={}] to user cart [targetUserId={}]", shoppingCartItemRequestDTO.getProductId(), shoppingCartItem.getQuantity(), userId);
        shoppingCartItemRepository.save(shoppingCartItem);
        log.info("CREATED shopping cart item [itemId={}]", shoppingCartItem.getId());
        return shoppingCartItem;
    }

    @Override
    @Transactional
    public ShoppingCartItem updateItemQuantity(ShoppingCartItemUpdateRequestDTO shoppingCartItemUpdateRequestDTO, Long userId, Long productId) {
        if (shoppingCartItemUpdateRequestDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        ShoppingCart cart = shoppingCartService.getCartOrThrow(userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found"));

        if (shoppingCartItemUpdateRequestDTO.getQuantity() > product.getStockQuantity()) {
            throw new IllegalArgumentException("Insufficient stock");
        }

        ShoppingCartItem item = shoppingCartItemRepository
                .findByShoppingCartIdAndProductIdAndShoppingCartUserId(
                        cart.getId(), productId, userId
                ).orElseThrow(() -> new NoResourceFoundException("No item found"));


        item.setQuantity(shoppingCartItemUpdateRequestDTO.getQuantity());
        log.info("UPDATED quantity [quantity={}] for product [productId={}] for item [itemId={}]", item.getQuantity(), productId, item.getId());
        return item;
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long productId, Long userId) {
        ShoppingCart cart = shoppingCartService.getCartOrThrow(userId);

        ShoppingCartItem item = shoppingCartItemRepository
                .findByShoppingCartIdAndProductIdAndShoppingCartUserId(
                        cart.getId(), productId, userId
                ).orElseThrow(() -> new NoResourceFoundException("No item found"));

        cart.removeItem(item);
        log.info("REMOVED product [productId={}] from user cart [itemId={}, userId={}]", productId, item.getId(), userId);
    }

    @Override
    public Page<ShoppingCartItem> findItemsByUser(Long userId, Pageable pageable) {
        ShoppingCart cart = shoppingCartService.getCartOrThrow(userId);
        Page<ShoppingCartItem> items = shoppingCartItemRepository.findByShoppingCartId(cart.getId(), pageable);
        log.info("FETCHED {} items from user cart [targetUserId={}]", items.getTotalElements(), userId);

        return items;
    }

    @Override
    public ShoppingCartItem findItemByUserAndProduct(Long productId, Long userId) {
        ShoppingCart cart = shoppingCartService.getCartOrThrow(userId);

        ShoppingCartItem item = shoppingCartItemRepository.findByShoppingCartIdAndProductIdAndShoppingCartUserId(cart.getId(), productId, userId)
                .orElseThrow(() -> new NoResourceFoundException("No item found"));

        log.info("FETCHED product [productId={}] from user cart [targetUserId={}]", productId, userId);

        return item;
    }

}
