package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Mapper.CategoryMapper;
import com.example.EcommerceBackendProject.Mapper.ProductMapper;
import com.example.EcommerceBackendProject.Repository.CategoryRepository;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Service.CategoryService;
import com.example.EcommerceBackendProject.Service.ProductService;
import com.example.EcommerceBackendProject.Service.ShoppingCartItemService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShoppingCartItemService shoppingCartItemService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public Integer findProductQuantityWithProductId(Long productId) {
        return productRepository.findStockQuantityByProductId(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found."));
    }

    @Override
    public Product createProduct(ProductRequestDTO productRequestDTO) {
        Product product = ProductMapper.toEntity(productRequestDTO);
        product.setCategories(categoryService.resolveCategories(productRequestDTO.getCategories()));
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(ProductRequestDTO productRequestDTO, Long productId) {
        Product productUpdate = productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found!"));

        productUpdate.setProductName(productRequestDTO.getProductName());
        productUpdate.setPrice(productRequestDTO.getPrice());
        productUpdate.setImageUrl(productRequestDTO.getImageUrl());
        productUpdate.setDescription(productRequestDTO.getDescription());
        productUpdate.setStockQuantity(productRequestDTO.getStockQuantity());

        productUpdate.setCategories(categoryService.resolveCategories(productRequestDTO.getCategories()));

        return productRepository.save(productUpdate);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found"));

        shoppingCartItemService.deleteItemsByProduct(productId);
        productRepository.delete(product);
    }

    @Override
    public BigDecimal findProductPrice(Long productId) {
        return productRepository.findPriceById(productId).orElseThrow(() -> new NoResourceFoundException("No product found"));
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
