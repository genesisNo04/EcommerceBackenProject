package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.Entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CategoryService {

    Category createCategory(Category category);

    List<Category> findCategoriesByProductId(Long productId);

    void deleteCategory(Long categoryId);
}
