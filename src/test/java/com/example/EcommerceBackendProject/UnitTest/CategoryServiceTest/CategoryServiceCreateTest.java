package com.example.EcommerceBackendProject.UnitTest.CategoryServiceTest;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.CategoryTestUtils.*;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.createTestProduct;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceCreateTest extends BaseCategoryServiceTest{

    @Test
    void createCategory() {
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product.setId(1L);
        Product product1 = createTestProduct("PLAYSTATION", "playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product1.setId(2L);
        List<Product> products = List.of(product, product1);
        CategoryRequestDTO categoryRequestDTO = createTestCategoryDTO("ELECTRONIC", "electronic", Set.of(1L, 2L));

        when(categoryRepository.existsByName("ELECTRONIC")).thenReturn(false);
        when(productRepository.findAllById(Set.of(1L, 2L))).thenReturn(products);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category saved = categoryService.createCategory(categoryRequestDTO);

        assertEquals("ELECTRONIC", saved.getName());
        assertEquals("electronic", saved.getDescription());
        assertTrue(saved.getProducts().containsAll(products));

        verify(categoryRepository).existsByName("ELECTRONIC");
        verify(categoryRepository).save(any(Category.class));
        verify(productRepository).findAllById(Set.of(1L, 2L));
    }

    @Test
    void createCategory_existingCategory() {
        CategoryRequestDTO categoryRequestDTO = createTestCategoryDTO("ELECTRONIC", "electronic", Set.of(1L, 2L));
        when(categoryRepository.existsByName("ELECTRONIC")).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> categoryService.createCategory(categoryRequestDTO));

        assertEquals("Category is already existed with this name: " + categoryRequestDTO.getName(), ex.getMessage());

        verify(categoryRepository).existsByName("ELECTRONIC");
        verify(categoryRepository, never()).save(any(Category.class));
        verify(productRepository, never()).findAllById(Set.of(1L, 2L));
    }

    @Test
    void createCategory_productsNotFound() {
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");
        product.setId(1L);
        Product product1 = createTestProduct("PLAYSTATION", "playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product1.setId(2L);
        List<Product> products = List.of(product);
        CategoryRequestDTO categoryRequestDTO = createTestCategoryDTO("ELECTRONIC", "electronic", Set.of(1L, 2L));

        when(categoryRepository.existsByName("ELECTRONIC")).thenReturn(false);
        when(productRepository.findAllById(Set.of(1L, 2L))).thenReturn(products);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.createCategory(categoryRequestDTO));

        assertEquals("One or more products not found", ex.getMessage());

        verify(categoryRepository).existsByName("ELECTRONIC");
        verify(categoryRepository, never()).save(any(Category.class));
        verify(productRepository).findAllById(Set.of(1L, 2L));
    }

    @Test
    void createCategory_emptyProducts() {
        CategoryRequestDTO categoryRequestDTO = createTestCategoryDTO("ELECTRONIC", "electronic", Set.of());

        when(categoryRepository.existsByName("ELECTRONIC")).thenReturn(false);
        when(productRepository.findAllById(Set.of()))
                .thenReturn(List.of());
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category saved = categoryService.createCategory(categoryRequestDTO);

        assertEquals("ELECTRONIC", saved.getName());
        assertEquals("electronic", saved.getDescription());
        assertEquals(0, saved.getProducts().size());

        verify(categoryRepository).existsByName("ELECTRONIC");
        verify(categoryRepository).save(any(Category.class));
        verify(productRepository, never()).findAllById(Set.of());
    }

    @Test
    void resolveCategory() {
        Category category1 = createTestCategory("ENTERTAINMENT", "Entertainment");
        Category category2 = createTestCategory("ELECTRONIC", "Electronic");

        List<Category> categories = List.of(category1, category2);
        Set<Long> ids = Set.of(1L, 2L);

        when(categoryRepository.findAllById(ids)).thenReturn(categories);

        Set<Category> result = categoryService.resolveCategories(ids);

        assertTrue(result.containsAll(categories));

        verify(categoryRepository).findAllById(Set.of(1L, 2L));
    }

    @Test
    void resolveCategory_noCategoryFound() {
        Category category1 = createTestCategory("ENTERTAINMENT", "Entertainment");
        Category category2 = createTestCategory("ELECTRONIC", "Electronic");

        List<Category> categories = List.of(category1);
        Set<Long> ids = Set.of(1L, 2L);

        when(categoryRepository.findAllById(ids)).thenReturn(categories);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> categoryService.resolveCategories(ids));

        assertEquals("One or more categories is not found", ex.getMessage());

        verify(categoryRepository).findAllById(Set.of(1L, 2L));
    }
}
