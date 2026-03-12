package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.ProductService;

import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.DTO.ProductUpdateRequestDTO;
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
public class ProductServiceUpdateTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void updateProduct_success() {
        Category category = testDataHelper.createCategory("ELECTRONIC", "Electronic", Set.of());
        Category category1 = testDataHelper.createCategory("ENTERTAINMENT", "Entertainment", Set.of());

        Product product = testDataHelper.createProduct("PS5",
                "Playstation",
                100,
                Set.of(category.getId()),
                BigDecimal.valueOf(499.99),
                "url");

        ProductRequestDTO productRequestDTO = ProductTestFactory.createProductDTO("XBOX", "Xbox", 50, Set.of(category1.getId()), BigDecimal.valueOf(450.99), "urlupdate");

        Product createdProduct = productService.updateProduct(productRequestDTO, product.getId());

        Product savedProduct = productRepository.findById(createdProduct.getId()).orElseThrow();

        assertEquals("XBOX", savedProduct.getProductName());
        assertEquals("Xbox", savedProduct.getDescription());
        assertEquals(50, savedProduct.getStockQuantity());
        assertEquals(BigDecimal.valueOf(450.99), savedProduct.getPrice());
        assertEquals("urlupdate", savedProduct.getImageUrl());
        assertTrue(savedProduct.getCategories().stream().anyMatch(c -> c.getId().equals(category1.getId())));
        assertFalse(savedProduct.getCategories().stream().anyMatch(c -> c.getId().equals(category.getId())));
    }

    @Test
    void updateProduct_success_sameSetOfCategories() {
        Category category = testDataHelper.createCategory("ELECTRONIC", "Electronic", Set.of());

        Product product = testDataHelper.createProduct("PS5",
                "Playstation",
                100,
                Set.of(category.getId()),
                BigDecimal.valueOf(499.99),
                "url");

        ProductRequestDTO productRequestDTO = ProductTestFactory.createProductDTO("XBOX", "Xbox", 50, Set.of(category.getId()), BigDecimal.valueOf(450.99), "urlupdate");

        Product createdProduct = productService.updateProduct(productRequestDTO, product.getId());

        Product savedProduct = productRepository.findById(createdProduct.getId()).orElseThrow();

        assertEquals("XBOX", savedProduct.getProductName());
        assertEquals("Xbox", savedProduct.getDescription());
        assertEquals(50, savedProduct.getStockQuantity());
        assertEquals(BigDecimal.valueOf(450.99), savedProduct.getPrice());
        assertEquals("urlupdate", savedProduct.getImageUrl());
        assertTrue(savedProduct.getCategories().stream().anyMatch(c -> c.getId().equals(category.getId())));
    }

    @Test
    void updateProduct_failed_productNotFound() {
        ProductRequestDTO productRequestDTO = ProductTestFactory.createProductDTO("XBOX", "Xbox", 50, Set.of(), BigDecimal.valueOf(450.99), "urlupdate");

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.updateProduct(productRequestDTO, 999L));

        assertEquals("No product found", ex.getMessage());
    }

    @Test
    void updateProduct_failed_categoryNotFound() {
        Category category = testDataHelper.createCategory("ELECTRONIC", "Electronic", Set.of());

        Product product = testDataHelper.createProduct("PS5",
                "Playstation",
                100,
                Set.of(category.getId()),
                BigDecimal.valueOf(499.99),
                "url");

        ProductRequestDTO productRequestDTO = ProductTestFactory.createProductDTO("XBOX", "Xbox", 50, Set.of(999L), BigDecimal.valueOf(450.99), "urlupdate");

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.updateProduct(productRequestDTO, product.getId()));

        assertEquals("One or more categories is not found", ex.getMessage());
    }

    @Test
    void patchProduct_success() {
        Category category = testDataHelper.createCategory("ELECTRONIC", "Electronic", Set.of());
        Category category1 = testDataHelper.createCategory("ENTERTAINMENT", "Entertainment", Set.of());

        Product product = testDataHelper.createProduct("PS5",
                "Playstation",
                100,
                Set.of(category.getId()),
                BigDecimal.valueOf(499.99),
                "url");

        ProductUpdateRequestDTO productRequestDTO = ProductTestFactory.createUpdateProductDTO("XBOX", "Xbox", 50, Set.of(category1.getId()), BigDecimal.valueOf(450.99), "urlupdate");

        Product createdProduct = productService.patchProduct(productRequestDTO, product.getId());

        Product savedProduct = productRepository.findById(createdProduct.getId()).orElseThrow();

        assertEquals("XBOX", savedProduct.getProductName());
        assertEquals("Xbox", savedProduct.getDescription());
        assertEquals(50, savedProduct.getStockQuantity());
        assertEquals(BigDecimal.valueOf(450.99), savedProduct.getPrice());
        assertEquals("urlupdate", savedProduct.getImageUrl());
        assertTrue(savedProduct.getCategories().stream().anyMatch(c -> c.getId().equals(category1.getId())));
        assertFalse(savedProduct.getCategories().stream().anyMatch(c -> c.getId().equals(category.getId())));
    }

    @Test
    void patchProduct_success_partialUpdate() {
        Category category = testDataHelper.createCategory("ELECTRONIC", "Electronic", Set.of());
        Category category1 = testDataHelper.createCategory("ENTERTAINMENT", "Entertainment", Set.of());

        Product product = testDataHelper.createProduct("PS5",
                "Playstation",
                100,
                Set.of(category.getId()),
                BigDecimal.valueOf(499.99),
                "url");

        ProductUpdateRequestDTO productRequestDTO = ProductTestFactory.createUpdateProductDTO("XBOX", null, null, Set.of(category1.getId()), null, "urlupdate");

        Product createdProduct = productService.patchProduct(productRequestDTO, product.getId());

        Product savedProduct = productRepository.findById(createdProduct.getId()).orElseThrow();

        assertEquals("XBOX", savedProduct.getProductName());
        assertEquals("Playstation", savedProduct.getDescription());
        assertEquals(100, savedProduct.getStockQuantity());
        assertEquals(BigDecimal.valueOf(499.99), savedProduct.getPrice());
        assertEquals("urlupdate", savedProduct.getImageUrl());
        assertTrue(savedProduct.getCategories().stream().anyMatch(c -> c.getId().equals(category1.getId())));
        assertFalse(savedProduct.getCategories().stream().anyMatch(c -> c.getId().equals(category.getId())));
    }

    @Test
    void patchProduct_failed_productNotFound() {
        ProductUpdateRequestDTO productRequestDTO = ProductTestFactory.createUpdateProductDTO("XBOX", "Xbox", 50, Set.of(), BigDecimal.valueOf(450.99), "urlupdate");

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.patchProduct(productRequestDTO, 999L));

        assertEquals("No product found", ex.getMessage());
    }

    @Test
    void patchProduct_failed_categoryNotFound() {
        Category category = testDataHelper.createCategory("ELECTRONIC", "Electronic", Set.of());

        Product product = testDataHelper.createProduct("PS5",
                "Playstation",
                100,
                Set.of(category.getId()),
                BigDecimal.valueOf(499.99),
                "url");

        ProductUpdateRequestDTO productRequestDTO = ProductTestFactory.createUpdateProductDTO("XBOX", "Xbox", 50, Set.of(999L), BigDecimal.valueOf(450.99), "urlupdate");

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.patchProduct(productRequestDTO, product.getId()));

        assertEquals("One or more categories is not found", ex.getMessage());
    }
}
