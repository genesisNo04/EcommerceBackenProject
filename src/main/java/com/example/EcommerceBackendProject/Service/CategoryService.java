package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.Entity.Category;

import java.util.List;

public interface CategoryService {

    Category createCategory(Category category);

    List<Category> findCategoriesByProductId(Long productId);
}
