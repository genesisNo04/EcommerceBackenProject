package com.example.EcommerceBackendProject.UnitTest.ProductServiceTest;

import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.CategoryTestUtils.createCategory;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceReadTest extends BaseProductServiceTest {

    @Test
    void findProductQuantity() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");

        when(productRepository.findStockQuantityByProductId(1L)).thenReturn(Optional.of(product.getStockQuantity()));

        Integer quantity = productService.findProductQuantityWithProductId(1L);

        assertEquals(100, quantity);
        verify(productRepository).findStockQuantityByProductId(1L);
    }

    @Test
    void findProductQuantity_productNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.findProductQuantityWithProductId(1L));

        assertEquals("No product found", ex.getMessage());
        verify(productRepository).findStockQuantityByProductId(1L);
    }

}
