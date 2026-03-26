package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.ProductService;

import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.ProductTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
import com.example.EcommerceBackendProject.Service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductServiceCreateTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void createProduct_success() {
        Category category = testDataHelper.createCategory();

        ProductRequestDTO productRequestDTO = ProductTestFactory.createProductDTO("PS5", "Playstation", 100, Set.of(category.getId()), BigDecimal.valueOf(499.99), "url");

        Product createdProduct = productService.createProduct(productRequestDTO);

        Product savedProduct = productRepository.findById(createdProduct.getId()).orElseThrow();

        assertEquals("PS5", savedProduct.getProductName());
        assertEquals("Playstation", savedProduct.getDescription());
        assertEquals(100, savedProduct.getStockQuantity());
        assertEquals(BigDecimal.valueOf(499.99), savedProduct.getPrice());
        assertEquals("url", savedProduct.getImageUrl());
        assertTrue(savedProduct.getCategories().stream().anyMatch(c -> c.getId().equals(category.getId())));
    }

    @Test
    void createProduct_failed_categoryNotFound() {
        ProductRequestDTO productRequestDTO = ProductTestFactory.createProductDTO("PS5", "Playstation", 100, Set.of(999L), BigDecimal.valueOf(499.99), "url");

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.createProduct(productRequestDTO));

        assertEquals("One or more categories is not found", ex.getMessage());
    }
}
