package com.example.EcommerceBackendProject.UnitTest.CategoryServiceTest;

import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.CategoryTestUtils.createTestCategory;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.createTestProduct;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceDeleteTest extends BaseCategoryServiceTest{

    @Test
    void deleteCategory() {
        Category category = createTestCategory("ENTERTAINMENT", "Entertainment");
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product.addCategory(category);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);
        assertFalse(product.getCategories().contains(category));

        verify(categoryRepository).findById(1L);
        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_categoryNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.deleteCategory(1L));

        assertEquals("No category with id: " + 1L, ex.getMessage());

        verify(categoryRepository).findById(1L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
