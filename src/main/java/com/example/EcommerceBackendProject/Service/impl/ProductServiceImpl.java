package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.DTO.ProductUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Mapper.ProductMapper;
import com.example.EcommerceBackendProject.Repository.CategoryRepository;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Repository.ShoppingCartItemRepository;
import com.example.EcommerceBackendProject.Service.CategoryService;
import com.example.EcommerceBackendProject.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
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
        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(productRequestDTO.getCategoriesId()));

        if (categories.size() != productRequestDTO.getCategoriesId().size()) {
            throw new NoResourceFoundException("One or more categories is not found");
        }

        Product product = ProductMapper.toEntity(productRequestDTO);
        product.getCategories().addAll(categories);

        return productRepository.save(product);
    }

    @Override
    @Transactional
    public Product updateProduct(ProductRequestDTO productRequestDTO, Long productId) {
        Product productUpdate = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found!"));

        productUpdate.setProductName(productRequestDTO.getProductName());
        productUpdate.setPrice(productRequestDTO.getPrice());
        productUpdate.setImageUrl(productRequestDTO.getImageUrl());
        productUpdate.setDescription(productRequestDTO.getDescription());
        productUpdate.setStockQuantity(productRequestDTO.getStockQuantity());

        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(productRequestDTO.getCategoriesId()));

        if (!productUpdate.getCategories().equals(categories)) {
            productUpdate.getCategories().clear();
            productUpdate.getCategories().addAll(categories);
        }

        return productUpdate;
    }

    @Override
    @Transactional
    public Product patchProduct(ProductUpdateRequestDTO productUpdateRequestDTO, Long productId) {
        Product productUpdate = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found!"));

        if (productUpdateRequestDTO.getCategoriesId() != null) {

            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(productUpdateRequestDTO.getCategoriesId()));

            if (categories.size() != productUpdateRequestDTO.getCategoriesId().size()) {
                throw new NoResourceFoundException("One or more categories is not found");
            }

            if (!productUpdate.getCategories().equals(categories)) {
                productUpdate.getCategories().clear();
                productUpdate.getCategories().addAll(categories);
            }
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
        Product product = productRepository.findByIdForUpdate(productId)
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
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void addCategory(Long productId, Long categoryId) {
        Product product = productRepository.findByIdForUpdate(productId).orElseThrow(() -> new NoResourceFoundException("No product found"));

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NoResourceFoundException("No category found"));

        product.addCategory(category);
    }

    @Override
    @Transactional
    public void removeCategory(Long productId, Long categoryId) {
        Product product = productRepository.findByIdForUpdate(productId).orElseThrow(() -> new NoResourceFoundException("No product found"));

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NoResourceFoundException("No category found"));

        product.removeCategory(category);
    }
}
