package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.DTO.CategoryUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Mapper.CategoryMapper;
import com.example.EcommerceBackendProject.Repository.CategoryRepository;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Category createCategory(CategoryRequestDTO categoryRequestDTO) {
        if (categoryRepository.existsByName(categoryRequestDTO.getName())) {
            throw new ResourceAlreadyExistsException("Category is already existed with this name: " + categoryRequestDTO.getName());
        }
        Category category = CategoryMapper.toEntity(categoryRequestDTO);

        if (!categoryRequestDTO.getProductIds().isEmpty()) {
            List<Product> products = productRepository.findAllById(categoryRequestDTO.getProductIds());

            if (products.size() < categoryRequestDTO.getProductIds().size()) {
                throw new NoResourceFoundException("One or more products not found");
            }

            products.forEach(product -> product.addCategory(category));
        }

        Category saved = categoryRepository.save(category);

        log.info("CREATED category [categoryId={}]", saved.getId());

        return saved;
    }

    @Override
    public List<Category> findCategoriesByProductId(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product with id: " + productId));

        log.info("FETCHED category by [productId={}]", productId);

        return categoryRepository.findByProductId(productId);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoResourceFoundException("No category with id: " + categoryId));

        for (Product product : new HashSet<>(category.getProducts())) {
            product.removeCategory(category);
        }

        log.info("DELETED category [categoryId={}]", categoryId);

        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public Set<Category> resolveCategories(Set<Long> categoryIds) {
        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(categoryIds));

        if (categories.size() != categoryIds.size()) {
            throw new NoResourceFoundException("One or more categories is not found");
        }

        return categories;
    }

    @Override
    public Category findByName(String name) {
        Category category = categoryRepository.findByName(name).orElseThrow(() -> new NoResourceFoundException("No category with name: " + name));
        log.info("FETCHED category by [categoryName={}] return found [categoryId={}]", name, category.getId());
        return category;
    }

    @Override
    @Transactional
    public Category updateCategory(Long categoryId, CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoResourceFoundException("No Category with id: " + categoryId));

        if (!categoryRequestDTO.getName().equals(category.getName()) && categoryRepository.existsByName(categoryRequestDTO.getName())) {
            throw new ResourceAlreadyExistsException("Category already exist with name: " + categoryRequestDTO.getName());
        }

        for (Product product : new HashSet<>(category.getProducts())) {
            product.removeCategory(category);
        }

        category.setName(categoryRequestDTO.getName());
        category.setDescription(categoryRequestDTO.getDescription());

        Set<Product> products = categoryRequestDTO.getProductIds().stream().map(id -> productRepository.findById(id)
                .orElseThrow(() -> new NoResourceFoundException("No product with id: " + id))).collect(Collectors.toSet());

        products.forEach(p -> p.addCategory(category));

        log.info("UPDATED category [categoryId={}]", categoryId);

        return category;
    }

    @Override
    @Transactional
    public Category patchCategory(Long categoryId, CategoryUpdateRequestDTO categoryUpdateRequestDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoResourceFoundException("No Category with id: " + categoryId));
        if (categoryUpdateRequestDTO.getName() != null) {
            category.setName(categoryUpdateRequestDTO.getName());
        }

        if (categoryUpdateRequestDTO.getDescription() != null) {
            category.setDescription(categoryUpdateRequestDTO.getDescription());
        }

        if (categoryUpdateRequestDTO.getProductIds() != null) {
            category.getProducts().forEach(product -> product.removeCategory(category));
            category.getProducts().clear();

            Set<Product> products = categoryUpdateRequestDTO.getProductIds().stream().map(id -> productRepository.findById(id)
                    .orElseThrow(() -> new NoResourceFoundException("No Product with id: " + id))).collect(Collectors.toSet());
            products.forEach(p -> p.addCategory(category));
        }

        log.info("PATCHED category [categoryId={}]", categoryId);

        return category;
    }

    @Override
    public Page<Category> findCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        log.info("FETCHED categories, total={}", categories.getTotalElements());
        return categories;
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NoResourceFoundException("No Category with id: " + id));
    }
}
