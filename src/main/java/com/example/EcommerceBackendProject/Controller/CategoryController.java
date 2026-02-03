package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.DTO.CategoryResponseDTO;
import com.example.EcommerceBackendProject.DTO.CategoryUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Mapper.CategoryMapper;
import com.example.EcommerceBackendProject.Service.CategoryService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final PageableSortValidator pageableSortValidator;

    public CategoryController(CategoryService categoryService, PageableSortValidator pageableSortValidator) {
        this.categoryService = categoryService;
        this.pageableSortValidator = pageableSortValidator;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryService.createCategory(categoryRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryMapper.toDTO(category));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CategoryResponseDTO>> findCategoryByProductId(@PathVariable Long productId) {
        List<Category> category = categoryService.findCategoriesByProductId(productId);
        List<CategoryResponseDTO> responseDTOS = category.stream().map(CategoryMapper::toDTO).toList();
        return ResponseEntity.ok(responseDTOS);
    }

    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<Page<CategoryResponseDTO>> findCategories(@PageableDefault(size = 10, sort = {"name", "id"}) Pageable pageable) {
        pageable = pageableSortValidator.validate(pageable, SortableFields.CATEGORY.getFields());
        Page<Category> categories = categoryService.findCategories(pageable);
        Page<CategoryResponseDTO> response = categories.map(CategoryMapper::toDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryService.updateCategory(categoryId, categoryRequestDTO);
        return ResponseEntity.ok(CategoryMapper.toDTO(category));
    }

    @PatchMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponseDTO> partiallyUpdateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryUpdateRequestDTO categoryUpdateRequestDTO) {
        Category category = categoryService.patchCategory(categoryId, categoryUpdateRequestDTO);
        return ResponseEntity.ok(CategoryMapper.toDTO(category));
    }
}
