package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Mapper.CategoryMapper;
import com.example.EcommerceBackendProject.Repository.CategoryRepository;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Service.CategoryService;
import jakarta.transaction.Transactional;
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
    public Category createCategory(CategoryRequestDTO categoryRequestDTO) {
        if (categoryRepository.existsByName(categoryRequestDTO.getName())) {
            throw new ResourceAlreadyExistsException("Category is already existed with this name: " + categoryRequestDTO.getName());
        }

        return categoryRepository.save(CategoryMapper.toEntity(categoryRequestDTO));
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

        category.getProducts().forEach(product -> product.getCategories().remove(category));
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

        Set<Category> categories = categoryRequestDTOs.stream()
                .map(category -> existingCategories.getOrDefault(category.getName(),
                        categoryRepository.save(CategoryMapper.toEntity(category))))
                .collect(Collectors.toSet());

        return categories;
    }

    @Override
    public Category findByName(String name) {
        return categoryRepository.findByName(name).orElseThrow(() -> new NoResourceFoundException("No category found!"));
    }
}
