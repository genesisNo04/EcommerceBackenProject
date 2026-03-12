package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.ProductService;

import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
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
public class ProductServiceDeleteTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void deleteProduct_success() {
        Category category = testDataHelper.createCategory("ELECTRONIC", "Electronic", Set.of());

        Product product = testDataHelper.createProduct("PS5",
                "Playstation",
                100,
                Set.of(category.getId()),
                BigDecimal.valueOf(499.99),
                "url");

        productService.deleteProduct(product.getId());

        Product savedProduct = productRepository.findById(product.getId()).orElse(null);

        assertNull(savedProduct);
        assertFalse(category.getProducts().stream().anyMatch(p -> p.getId().equals(product.getId())));
    }

    @Test
    void deleteProduct_failed_productNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.deleteProduct(999L));

        assertEquals("No product found", ex.getMessage());
    }
}
