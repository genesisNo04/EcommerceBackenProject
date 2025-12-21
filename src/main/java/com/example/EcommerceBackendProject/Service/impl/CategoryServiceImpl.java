package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Repository.CategoryRepository;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Service.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Category createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new ResourceAlreadyExistsException("Category is already existed with this name: " + category.getName());
        }
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

        category.getProducts().forEach(product -> product.getCategories().remove(category));
        categoryRepository.delete(category);
    }
}
