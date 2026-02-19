package com.example.EcommerceBackendProject.UnitTest.ProductServiceTest;

import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.CategoryTestUtils.*;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.*;
import static org.mockito.Mockito.*;

public class ProductServiceCreateTest extends BaseProductServiceTest{

    @Test
    void createProduct() {
        Category category = createCategory("ELECTRONIC", "electronic");
        Category category1 = createCategory("FOOD", "food");
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setCategories(Set.of(category, category1));
        ProductRequestDTO productRequestDTO = createTestProductDTO("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");

        when(categoryRepository.findAllById(Set.of(1L, 2L))).thenReturn(List.of(category, category1));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product savedProduct = productService.createProduct(productRequestDTO);

        assertEquals("PS5", savedProduct.getProductName());
        assertEquals("Playstation", savedProduct.getDescription());
        assertEquals(BigDecimal.valueOf(499.99), savedProduct.getPrice());
        assertEquals(100, savedProduct.getStockQuantity());
        assertEquals("testurl", savedProduct.getImageUrl());
        assertEquals(2, savedProduct.getCategories().size());

        verify(productRepository).save(any(Product.class));
        verify(categoryRepository).findAllById(Set.of(1L, 2L));
    }
}
