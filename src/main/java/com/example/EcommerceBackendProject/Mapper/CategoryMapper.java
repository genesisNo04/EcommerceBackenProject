package com.example.EcommerceBackendProject.Mapper;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.DTO.CategoryResponseDTO;
import com.example.EcommerceBackendProject.Entity.Category;

public class CategoryMapper {

    public static CategoryResponseDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryResponseDTO categoryResponseDTO = new CategoryResponseDTO();
        categoryResponseDTO.setId(category.getId());
        categoryResponseDTO.setName(category.getName());
        categoryResponseDTO.setDescription(category.getDescription());
        return categoryResponseDTO;
    }

    public static Category toEntity(CategoryRequestDTO categoryRequestDTO) {
        Category category = new Category();
        category.setName(categoryRequestDTO.getName());
        category.setDescription(categoryRequestDTO.getDescription());

        return category;
    }
}
