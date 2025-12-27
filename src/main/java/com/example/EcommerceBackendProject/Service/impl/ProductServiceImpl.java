package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Service.ProductService;
import com.example.EcommerceBackendProject.Service.ShoppingCartItemService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShoppingCartItemService shoppingCartItemService;

    @Override
    public Integer findProductQuantityWithProductId(Long productId) {
        return productRepository.findStockQuantityByProductId(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found."));
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product, Long productId) {
        Product productUpdate = productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found!"));

        productUpdate.setProductName(product.getProductName());
        productUpdate.setCategories(product.getCategories());
        productUpdate.setPrice(product.getPrice());
        productUpdate.setImageUrl(product.getImageUrl());
        productUpdate.setDescription(product.getDescription());
        productUpdate.setStockQuantity(product.getStockQuantity());

        return productRepository.save(productUpdate);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found"));

        shoppingCartItemService.deleteItemsByProduct(productId);
    }

    @Override
    public BigDecimal findProductPrice(Long productId) {
        return productRepository.findPriceById(productId);
    }

    @Override
    public Page<Product> findProductByKeyword(String keyword, Pageable pageable) {
        return productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
    }

    @Override
    public Page<Product> findProductByCategory(Category category, Pageable pageable) {
        return productRepository.findByCategories(category, pageable);
    }
}
