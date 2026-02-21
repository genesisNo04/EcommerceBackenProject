package com.example.EcommerceBackendProject.UnitTest.CategoryServiceTest;

import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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
}
