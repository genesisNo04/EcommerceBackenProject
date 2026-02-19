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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final ProductRepository productRepository;
    private final ShoppingCartItemRepository shoppingCartItemRepository;
    private final CategoryService categoryService;

    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductRepository productRepository, ShoppingCartItemRepository shoppingCartItemRepository, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.shoppingCartItemRepository = shoppingCartItemRepository;
        this.categoryService = categoryService;
    }

    @Override
    public Integer findProductQuantityWithProductId(Long productId) {
        Integer quantity = productRepository.findStockQuantityByProductId(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found"));

        log.info("FETCHED stock quantity for product [productId={}]", productId);

        return quantity;
    }

    @Override
    @Transactional
    public Product createProduct(ProductRequestDTO productRequestDTO) {
        Set<Category> categories = categoryService.resolveCategories(productRequestDTO.getCategoriesId());

        Product product = ProductMapper.toEntity(productRequestDTO);
        product.getCategories().addAll(categories);
        productRepository.save(product);

        log.info("CREATED product [productId={}]", product.getId());

        return product;
    }

    @Override
    @Transactional
    public Product updateProduct(ProductRequestDTO productRequestDTO, Long productId) {
        Product productUpdate = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found"));

        productUpdate.setProductName(productRequestDTO.getProductName());
        productUpdate.setPrice(productRequestDTO.getPrice());
        productUpdate.setImageUrl(productRequestDTO.getImageUrl());
        productUpdate.setDescription(productRequestDTO.getDescription());
        productUpdate.setStockQuantity(productRequestDTO.getStockQuantity());

        Set<Category> categories = categoryService.resolveCategories(productRequestDTO.getCategoriesId());

        if (!productUpdate.getCategories().equals(categories)) {
            productUpdate.getCategories().clear();
            productUpdate.getCategories().addAll(categories);
        }

        log.info("UPDATED product [productId={}]", productUpdate.getId());

        return productUpdate;
    }

    @Override
    @Transactional
    public Product patchProduct(ProductUpdateRequestDTO productUpdateRequestDTO, Long productId) {
        Product productUpdate = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found"));

        if (productUpdateRequestDTO.getCategoriesId() != null) {

            Set<Category> categories = categoryService.resolveCategories(productUpdateRequestDTO.getCategoriesId());

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

        log.info("PATCHED product [productId={}]", productUpdate.getId());

        return productUpdate;
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product found"));

        shoppingCartItemRepository.deleteByProductId(productId);
        productRepository.delete(product);

        log.info("DELETED product [productId={}]", product.getId());
    }

    @Override
    public BigDecimal findProductPrice(Long productId) {
        BigDecimal price = productRepository.findPriceById(productId).orElseThrow(() -> new NoResourceFoundException("No product found"));
        log.info("FETCHED price for product [productId={}] [price={}]", productId, price);
        return price;
    }

    @Override
    public Page<Product> findProductByKeyword(String keyword, Pageable pageable) {
        Page<Product> products = productRepository.findByProductNameContainingIgnoreCase(keyword, pageable);
        log.info("FETCHED products with keyword [keyword={}] [total={}]", keyword, products.getTotalElements());
        return products;
    }

    @Override
    public Page<Product> findProductByCategory(Category category, Pageable pageable) {
        Page<Product> products = productRepository.findByCategories(category, pageable);
        log.info("FETCHED products with category [category={}] [total={}]", category.getName(), products.getTotalElements());
        return products;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAll(Pageable pageable) {
        Page<Product> products = productRepository.findAll(pageable);
        log.info("FETCHED products [total={}]", products.getTotalElements());
        return products;
    }

    @Override
    @Transactional
    public void addCategory(Long productId, Long categoryId) {
        Product product = productRepository.findByIdForUpdate(productId).orElseThrow(() -> new NoResourceFoundException("No product found"));

        Category category = categoryService.findById(categoryId);

        product.addCategory(category);
        log.info("ADDED category [categoryId={}] to product [productId={}]", categoryId, productId);
    }

    @Override
    @Transactional
    public void removeCategory(Long productId, Long categoryId) {
        Product product = productRepository.findByIdForUpdate(productId).orElseThrow(() -> new NoResourceFoundException("No product found"));

        Category category = categoryService.findById(categoryId);

        product.removeCategory(category);
        log.info("REMOVED category [categoryId={}] from product [productId={}]", categoryId, productId);
    }
}
