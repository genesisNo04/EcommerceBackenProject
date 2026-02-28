package com.example.EcommerceBackendProject.UnitTest.ProductServiceTest;

import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Set;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.CategoryTestUtils.*;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.*;
import static org.mockito.Mockito.*;

public class ProductServiceCreateTest extends BaseProductServiceTest{

    @Test
    void createProduct() {
        Category category = createTestCategory("ELECTRONIC", "electronic");
        Category category1 = createTestCategory("FOOD", "food");
        Set<Category> categories = Set.of(category, category1);
        ProductRequestDTO productRequestDTO = createTestProductDTO("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, Set.of(1L, 2L), "testurl");

        when(categoryService.resolveCategories(Set.of(1L, 2L))).thenReturn(categories);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product savedProduct = productService.createProduct(productRequestDTO);

        assertEquals("PS5", savedProduct.getProductName());
        assertEquals("Playstation", savedProduct.getDescription());
        assertEquals(BigDecimal.valueOf(499.99), savedProduct.getPrice());
        assertEquals(100, savedProduct.getStockQuantity());
        assertEquals("testurl", savedProduct.getImageUrl());
        assertTrue(savedProduct.getCategories().containsAll(categories));

        verify(productRepository).save(any(Product.class));
        verify(categoryService).resolveCategories(Set.of(1L, 2L));
    }

    @Test
    void createProduct_categoryNotFound() {
        ProductRequestDTO productRequestDTO = createTestProductDTO("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, Set.of(1L, 2L), "testurl");

        when(categoryService.resolveCategories(Set.of(1L, 2L))).thenThrow(new NoResourceFoundException("One or more categories is not found"));
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.createProduct(productRequestDTO));

        assertEquals("One or more categories is not found", ex.getMessage());
        verify(productRepository, never()).save(any(Product.class));
        verify(categoryService).resolveCategories(Set.of(1L, 2L));
    }
}
