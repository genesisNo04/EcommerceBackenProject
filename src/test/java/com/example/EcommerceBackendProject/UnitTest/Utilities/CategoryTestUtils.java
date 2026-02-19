package com.example.EcommerceBackendProject.UnitTest.Utilities;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Mapper.CategoryMapper;

import java.util.HashSet;

public class CategoryTestUtils {

    public static Category createCategory(String name, String description) {
        return CategoryMapper.toEntity(new CategoryRequestDTO(name, description, new HashSet<>()));
    }
}
