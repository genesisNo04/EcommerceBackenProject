package com.example.EcommerceBackendProject.UnitTest.ProductServiceTest;

import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.DTO.ProductUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.CategoryTestUtils.createTestCategory;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceUpdateTest extends BaseProductServiceTest{

    @Test
    void updateProduct() {
        Category category = createTestCategory("ELECTRONIC", "electronic");
        Category category1 = createTestCategory("FOOD", "food");
        Set<Category> categories = Set.of(category, category1);
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        ProductRequestDTO productRequestDTO = createTestProductDTO("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, Set.of(1L, 2L), "testurl");

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(categoryService.resolveCategories(Set.of(1L, 2L))).thenReturn(categories);

        Product savedProduct = productService.updateProduct(productRequestDTO, 1L);

        assertEquals("PS5", savedProduct.getProductName());
        assertEquals("Playstation", savedProduct.getDescription());
        assertEquals(BigDecimal.valueOf(499.99), savedProduct.getPrice());
        assertEquals(100, savedProduct.getStockQuantity());
        assertEquals("testurl", savedProduct.getImageUrl());
        assertTrue(savedProduct.getCategories().containsAll(categories));

        verify(categoryService).resolveCategories(Set.of(1L, 2L));
    }

    @Test
    void updateProduct_noProductFound() {
        ProductRequestDTO productRequestDTO = createTestProductDTO("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, Set.of(1L, 2L), "testurl");


        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.updateProduct(productRequestDTO, 1L));

        assertEquals("No product found", ex.getMessage());

        verify(productRepository).findByIdForUpdate(1L);
        verify(categoryService, never()).resolveCategories(Set.of(1L, 2L));
    }

    @Test
    void updateProduct_categoriesMissing() {
        ProductRequestDTO productRequestDTO = createTestProductDTO("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, Set.of(1L, 2L), "testurl");
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(categoryService.resolveCategories(Set.of(1L, 2L))).thenThrow(new NoResourceFoundException("One or more categories is not found"));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.updateProduct(productRequestDTO, 1L));

        assertEquals("One or more categories is not found", ex.getMessage());

        verify(productRepository).findByIdForUpdate(1L);
        verify(categoryService).resolveCategories(Set.of(1L, 2L));
    }

    @Test
    void updateProduct_updateCategory() {
        Category category = createTestCategory("ELECTRONIC", "electronic");
        Category category1 = createTestCategory("FOOD", "food");
        Category category2 = createTestCategory("KITCHEN", "kitchen");
        Category category3 = createTestCategory("BATH", "BATH");
        Set<Category> categories1 = new HashSet<>(Set.of(category, category1));
        Set<Category> categories2 = new HashSet<>(Set.of(category2, category3));

        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product.setCategories(categories1);
        ProductRequestDTO productRequestDTO = createTestProductDTO("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, Set.of(3L, 4L), "testurl");

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(categoryService.resolveCategories(Set.of(3L, 4L))).thenReturn(categories2);

        Product savedProduct = productService.updateProduct(productRequestDTO, 1L);

        assertTrue(savedProduct.getCategories().containsAll(categories2));
        assertFalse(savedProduct.getCategories().containsAll(categories1));

        verify(categoryService).resolveCategories(Set.of(3L, 4L));
    }

    @Test
    void patchProduct() {
        Category category = createTestCategory("ELECTRONIC", "electronic");
        Category category1 = createTestCategory("FOOD", "food");
        Set<Category> categories = Set.of(category, category1);
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product.setCategories(categories);
        ProductUpdateRequestDTO productUpdateRequestDTO = createTestUpdateProductDTO("PS5", "Playstation", null, null, null, null);

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        Product savedProduct = productService.patchProduct(productUpdateRequestDTO, 1L);

        assertEquals("PS5", savedProduct.getProductName());
        assertEquals("Playstation", savedProduct.getDescription());
        assertEquals(BigDecimal.valueOf(499.99), savedProduct.getPrice());
        assertEquals(50, savedProduct.getStockQuantity());
        assertEquals("testurl", savedProduct.getImageUrl());
        assertTrue(savedProduct.getCategories().containsAll(categories));

        verify(categoryService, never()).resolveCategories(Set.of(1L, 2L));
    }

    @Test
    void patchProduct_noProductFound() {
        ProductUpdateRequestDTO productUpdateRequestDTO = createTestUpdateProductDTO("PS5", "Playstation", null, null, null, null);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.patchProduct(productUpdateRequestDTO, 1L));

        assertEquals("No product found", ex.getMessage());
        verify(categoryService, never()).resolveCategories(Set.of(1L, 2L));
    }

    @Test
    void patchProduct_updateCategories() {
        Category category = createTestCategory("ELECTRONIC", "electronic");
        Category category1 = createTestCategory("FOOD", "food");
        Category category2 = createTestCategory("KITCHEN", "kitchen");
        Category category3 = createTestCategory("BATH", "BATH");
        Set<Category> categories1 = new HashSet<>(Set.of(category, category1));
        Set<Category> categories2 = new HashSet<>(Set.of(category2, category3));

        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product.setCategories(categories1);
        ProductUpdateRequestDTO productUpdateRequestDTO = createTestUpdateProductDTO("PS5", "Playstation", null, null, Set.of(3L, 4L), null);

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(categoryService.resolveCategories(Set.of(3L, 4L))).thenReturn(categories2);

        Product savedProduct = productService.patchProduct(productUpdateRequestDTO, 1L);

        assertTrue(savedProduct.getCategories().containsAll(categories2));
        assertFalse(savedProduct.getCategories().containsAll(categories1));

        verify(categoryService).resolveCategories(Set.of(3L, 4L));
    }

    @Test
    void addCategory() {
        Category category = createTestCategory("ENTERTAINMENT", "entertainment");

        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(categoryService.findById(1L)).thenReturn(category);

        productService.addCategory(1L, 1L);

        verify(productRepository).findByIdForUpdate(1L);
        verify(categoryService).findById(1L);

        assertTrue(product.getCategories().contains(category));
    }

    @Test
    void addCategory_noProductFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.addCategory(1L, 1L));

        assertEquals("No product found", ex.getMessage());

        verify(categoryService, never()).findById(1L);
    }

    @Test
    void addCategory_noCategoryFound() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(categoryService.findById(1L)).thenThrow(new NoResourceFoundException("No category found"));
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.addCategory(1L, 1L));

        assertEquals("No category found", ex.getMessage());
    }

    @Test
    void removeCategory() {
        Category category = createTestCategory("ENTERTAINMENT", "entertainment");

        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.addCategory(category);

        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(categoryService.findById(1L)).thenReturn(category);

        productService.removeCategory(1L, 1L);

        verify(productRepository).findByIdForUpdate(1L);
        verify(categoryService).findById(1L);

        assertFalse(product.getCategories().contains(category));
    }

    @Test
    void removeCategory_noProductFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.removeCategory(1L, 1L));

        assertEquals("No product found", ex.getMessage());

        verify(categoryService, never()).findById(1L);
    }

    @Test
    void removeCategory_noCategoryFound() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(categoryService.findById(1L)).thenThrow(new NoResourceFoundException("No category found"));
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.removeCategory(1L, 1L));

        assertEquals("No category found", ex.getMessage());
    }
}
