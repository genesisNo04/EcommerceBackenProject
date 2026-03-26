package com.example.EcommerceBackendProject.IntegrationTesting.Utilities;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.DTO.CategoryUpdateRequestDTO;

import java.util.Set;

public class CategoryTestFactory {

    public static CategoryRequestDTO createCategoryDTO(String name, String description, Set<Long> productIds) {
        return new CategoryRequestDTO(name, description, productIds);
    }

    public static CategoryUpdateRequestDTO createUpdateCategoryDTO(String name, String description, Set<Long> productIds) {
        return new CategoryUpdateRequestDTO(name, description, productIds);
    }
}
