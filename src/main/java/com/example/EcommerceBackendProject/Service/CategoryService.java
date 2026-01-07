package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

public interface CategoryService {

    Category createCategory(CategoryRequestDTO categoryRequestDTO);

    List<Category> findCategoriesByProductId(Long productId);

    void deleteCategory(Long categoryId);

    Set<Category> resolveCategories(Set<CategoryRequestDTO> categoryRequestDTOs);
}
