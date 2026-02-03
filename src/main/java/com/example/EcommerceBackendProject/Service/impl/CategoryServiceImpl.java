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
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional
    public Category createCategory(CategoryRequestDTO categoryRequestDTO) {
        if (categoryRepository.existsByName(categoryRequestDTO.getName())) {
            throw new ResourceAlreadyExistsException("Category is already existed with this name: " + categoryRequestDTO.getName());
        }
        Category category = CategoryMapper.toEntity(categoryRequestDTO);
        Set<Product> products = categoryRequestDTO.getProductIds().stream().map(id -> productRepository.findById(id)
                .orElseThrow(() -> new NoResourceFoundException("Product not found!"))).collect(Collectors.toSet());
        products.forEach(product -> product.addCategory(category));
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> findCategoriesByProductId(Long productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new NoResourceFoundException("No product with id: " + productId));
        return categoryRepository.findByProductId(productId);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoResourceFoundException("No category with id: " + categoryId));

        category.getProducts().forEach(product -> product.removeCategory(category));
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public Set<Category> resolveCategories(Set<CategoryRequestDTO> categoryRequestDTOs) {
        if (categoryRequestDTOs == null || categoryRequestDTOs.isEmpty()) {
            return Collections.emptySet();
        }

        Set<String> categoryNames = categoryRequestDTOs
                .stream()
                .map(CategoryRequestDTO::getName)
                .collect(Collectors.toSet());

        List<Category> existingCategoryList = categoryRepository.findAllByNameIn(categoryNames);
        Map<String, Category> existingCategories = existingCategoryList.stream()
                .collect(Collectors.toMap(Category::getName, c -> c));

        Set<Category> result = new HashSet<>();

        for (CategoryRequestDTO dto: categoryRequestDTOs) {
            Category category = existingCategories.get(dto.getName());

            if (category == null) {
                category = categoryRepository.save(CategoryMapper.toEntity(dto));
            }

            result.add(category);
        }

        return result;
    }

    @Override
    public Category findByName(String name) {
        return categoryRepository.findByName(name).orElseThrow(() -> new NoResourceFoundException("No category found!"));
    }

    @Override
    @Transactional
    public Category updateCategory(Long categoryId, CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoResourceFoundException("Category not found"));

        category.getProducts().forEach(product -> product.removeCategory(category));
        category.getProducts().clear();

        category.setName(categoryRequestDTO.getName());
        category.setDescription(categoryRequestDTO.getDescription());
        Set<Product> products = categoryRequestDTO.getProductIds().stream().map(id -> productRepository.findById(id)
                .orElseThrow(() -> new NoResourceFoundException("Product not found!"))).collect(Collectors.toSet());

        products.forEach(p -> p.addCategory(category));
        return category;
    }

    @Override
    @Transactional
    public Category patchCategory(Long categoryId, CategoryUpdateRequestDTO categoryUpdateRequestDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoResourceFoundException("Category not found"));
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
                    .orElseThrow(() -> new NoResourceFoundException("Product not found!"))).collect(Collectors.toSet());
            products.forEach(p -> p.addCategory(category));
        }

        return category;
    }

    @Override
    public Page<Category> findCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
}
