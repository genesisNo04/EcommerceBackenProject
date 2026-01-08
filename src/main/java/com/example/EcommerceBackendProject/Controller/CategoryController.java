package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.DTO.CategoryResponseDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Mapper.CategoryMapper;
import com.example.EcommerceBackendProject.Service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryService.createCategory(categoryRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryMapper.toDTO(category));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<List<CategoryResponseDTO>> findCategoryByProductId(@PathVariable Long productId) {
        List<Category> category = categoryService.findCategoriesByProductId(productId);
        List<CategoryResponseDTO> responseDTOS = category.stream().map(CategoryMapper::toDTO).toList();
        return ResponseEntity.ok(responseDTOS);
    }

}
