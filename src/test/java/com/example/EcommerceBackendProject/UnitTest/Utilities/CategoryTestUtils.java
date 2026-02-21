package com.example.EcommerceBackendProject.UnitTest.Utilities;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Mapper.CategoryMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryTestUtils {

    public static Category createTestCategory(String name, String description) {
        return CategoryMapper.toEntity(new CategoryRequestDTO(name, description, new HashSet<>()));
    }

    public static CategoryRequestDTO createTestCategoryDTO(String name, String description, Set<Long> ids) {
        return new CategoryRequestDTO(name, description, ids);
    }
}
