package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.DTO.CategoryUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;

import java.util.List;
import java.util.Set;

public interface CategoryService {

    Category createCategory(CategoryRequestDTO categoryRequestDTO);

    List<Category> findCategoriesByProductId(Long productId);

    void deleteCategory(Long categoryId);

    Set<Category> resolveCategories(Set<CategoryRequestDTO> categoryRequestDTOs);

    Category findByName(String name);

    Category updateCategory(Long categoryId, CategoryRequestDTO categoryRequestDTO);

    Category patchCategory(Long categoryId, CategoryUpdateRequestDTO categoryUpdateRequestDTO);
}
