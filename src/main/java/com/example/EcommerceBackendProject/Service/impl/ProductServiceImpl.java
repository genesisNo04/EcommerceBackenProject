package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.DTO.ProductUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Mapper.CategoryMapper;
import com.example.EcommerceBackendProject.Mapper.ProductMapper;
import com.example.EcommerceBackendProject.Repository.CategoryRepository;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Repository.ShoppingCartItemRepository;
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
    private ShoppingCartItemRepository shoppingCartItemRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Integer findProductQuantityWithProductId(Long productId) {
        return productRepository.findStockQuantityByProductId(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found."));
    }

    @Override
    @Transactional
    public Product createProduct(ProductRequestDTO productRequestDTO) {
        Product product = ProductMapper.toEntity(productRequestDTO);
        Set<Category> categories = categoryService.resolveCategories(productRequestDTO.getCategories());
        categories.forEach(c -> c.addProduct(product));
        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(ProductRequestDTO productRequestDTO, Long productId) {
        Product productUpdate = productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found!"));

        productUpdate.getCategories().forEach(c -> c.removeProduct(productUpdate));

        productUpdate.setProductName(productRequestDTO.getProductName());
        productUpdate.setPrice(productRequestDTO.getPrice());
        productUpdate.setImageUrl(productRequestDTO.getImageUrl());
        productUpdate.setDescription(productRequestDTO.getDescription());
        productUpdate.setStockQuantity(productRequestDTO.getStockQuantity());

        Set<Category> categories = categoryService.resolveCategories(productRequestDTO.getCategories());

        categories.forEach(c -> c.addProduct(productUpdate));

        return productUpdate;
    }

    @Override
    @Transactional
    public Product patchProduct(ProductUpdateRequestDTO productUpdateRequestDTO, Long productId) {
        Product productUpdate = productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found!"));

        if (productUpdateRequestDTO.getCategories() != null) {
            productUpdate.getCategories().forEach(c -> c.removeProduct(productUpdate));

            Set<Category> categories = categoryService.resolveCategories(productUpdateRequestDTO.getCategories());

            categories.forEach(c -> c.addProduct(productUpdate));
        }

        if (productUpdateRequestDTO.getProductName() != null) {
            productUpdate.setProductName(productUpdateRequestDTO.getProductName());
        }

        if (productUpdateRequestDTO.getPrice() != null) {
            productUpdate.setPrice(productUpdateRequestDTO.getPrice());
        }

        if (productUpdateRequestDTO.getImageUrl() != null) {
            productUpdate.setImageUrl(productUpdateRequestDTO.getImageUrl());
        }

        if (productUpdateRequestDTO.getDescription() != null) {
            productUpdate.setDescription(productUpdateRequestDTO.getDescription());
        }

        if (productUpdateRequestDTO.getStockQuantity() != null) {
            productUpdate.setStockQuantity(productUpdateRequestDTO.getStockQuantity());
        }

        return productUpdate;
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found"));

        shoppingCartItemRepository.deleteByProductId(productId);
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

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void addCategory(Long productId, Long categoryId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NoResourceFoundException("No product found"));

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NoResourceFoundException("No category found"));

        product.addCategory(category);
    }

    @Override
    @Transactional
    public void removeCategory(Long productId, Long categoryId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NoResourceFoundException("No product found"));

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NoResourceFoundException("No category found"));

        product.removeCategory(category);
    }
}
