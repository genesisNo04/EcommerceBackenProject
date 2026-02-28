package com.example.EcommerceBackendProject.UnitTest.CategoryServiceTest;

import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.CategoryTestUtils.*;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.createTestProduct;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CategoryServiceReadTest extends BaseCategoryServiceTest{

    @Test
    void findCategories_byProductId() {
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        Category category = createTestCategory("ELECTRONIC", "Electronic");
        Category category1 = createTestCategory("ENTERTAINMENT", "Entertainment");

        List<Category> categories = List.of(category, category1);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(categoryRepository.findByProductId(1L)).thenReturn(categories);

        List<Category> result = categoryService.findCategoriesByProductId(1L);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(categories));

        verify(productRepository).findById(1L);
        verify(categoryRepository).findByProductId(1L);
    }

    @Test
    void findCategories_noProductFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.findCategoriesByProductId(1L));

        assertEquals("No product with id: " + 1L, ex.getMessage());

        verify(productRepository).findById(1L);
        verify(categoryRepository, never()).findByProductId(1L);
    }

    @Test
    void findCategories_findByName() {
        Category category = createTestCategory("ELECTRONIC", "Electronic");

        when(categoryRepository.findByName("ELECTRONIC")).thenReturn(Optional.of(category));

        Category resultCategory = categoryService.findByName("ELECTRONIC");

        assertEquals("ELECTRONIC", resultCategory.getName());
        assertEquals("Electronic", resultCategory.getDescription());

        verify(categoryRepository).findByName("ELECTRONIC");
    }

    @Test
    void findCategories_findByName_categoryNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.findByName("ELECTRONIC"));

        assertEquals("No category with name: " + "ELECTRONIC", ex.getMessage());

        verify(categoryRepository).findByName("ELECTRONIC");
    }

    @Test
    void findCategories() {
        Pageable pageable = PageRequest.of(0, 10);
        Category category = createTestCategory("ELECTRONIC", "Electronic");
        Category category1 = createTestCategory("ENTERTAINMENT", "Entertainment");
        List<Category> categories = List.of(category, category1);

        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);

        Page<Category> response = categoryService.findCategories(pageable);

        assertEquals(2, response.getContent().size());
        assertEquals(2, response.getTotalElements());
        assertEquals(response.getTotalElements(), categories.size());

        verify(categoryRepository).findAll(pageable);
    }

    @Test
    void findById() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.findById(1L));

        assertEquals("No Category with id: " + 1L, ex.getMessage());

        verify(categoryRepository).findById(1L);
    }
}
