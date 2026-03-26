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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductServiceReadTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void searchPrice() {
        Product product = testDataHelper.createProduct("PS5",
                "Playstation",
                100,
                Set.of(),
                BigDecimal.valueOf(499.99),
                "url");

        BigDecimal price = productService.findProductPrice(product.getId());

        assertEquals(BigDecimal.valueOf(499.99), price);
    }

    @Test
    void searchPrice_noProductFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.findProductPrice(999L));

        assertEquals("No product found", ex.getMessage());
    }

    @Test
    void searchProduct_byKeyword() {
        Product product = testDataHelper.createProduct("Playstation 5",
                "Playstation",
                100,
                Set.of(),
                BigDecimal.valueOf(499.99),
                "url");
        Product product1 = testDataHelper.createProduct("PlayBox",
                "PlayBox",
                100,
                Set.of(),
                BigDecimal.valueOf(499.99),
                "url");
        Product product2 = testDataHelper.createProduct("XBOX",
                "XBox",
                100,
                Set.of(),
                BigDecimal.valueOf(499.99),
                "url");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> products = productService.findProductByKeyword("Play", pageable);

        assertEquals(2, products.getContent().size());
        assertEquals(2, products.getTotalElements());
        assertTrue(products.getContent().stream().anyMatch(p -> p.getId().equals(product.getId())));
        assertTrue(products.getContent().stream().anyMatch(p -> p.getId().equals(product1.getId())));
        assertFalse(products.getContent().stream().anyMatch(p -> p.getId().equals(product2.getId())));
    }

    @Test
    void searchProduct_byCategory() {
        Category category = testDataHelper.createCategory("ELECTRONIC", "Electronic", Set.of());
        Category category1 = testDataHelper.createCategory("ENTERTAINMENT", "Entertainment", Set.of());

        Product product = testDataHelper.createProduct("Playstation 5",
                "Playstation",
                100,
                Set.of(category.getId()),
                BigDecimal.valueOf(499.99),
                "url");
        Product product1 = testDataHelper.createProduct("PlayBox",
                "PlayBox",
                100,
                Set.of(category.getId()),
                BigDecimal.valueOf(499.99),
                "url");
        Product product2 = testDataHelper.createProduct("XBOX",
                "XBox",
                100,
                Set.of(category1.getId()),
                BigDecimal.valueOf(499.99),
                "url");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> products = productService.findProductByCategory(category, pageable);

        assertEquals(2, products.getContent().size());
        assertEquals(2, products.getTotalElements());
        assertTrue(products.getContent().stream().anyMatch(p -> p.getId().equals(product.getId())));
        assertTrue(products.getContent().stream().anyMatch(p -> p.getId().equals(product1.getId())));
        assertFalse(products.getContent().stream().anyMatch(p -> p.getId().equals(product2.getId())));
    }

    @Test
    void searchAllProduct() {
        Category category = testDataHelper.createCategory("ELECTRONIC", "Electronic", Set.of());
        Category category1 = testDataHelper.createCategory("ENTERTAINMENT", "Entertainment", Set.of());

        Product product = testDataHelper.createProduct("Playstation 5",
                "Playstation",
                100,
                Set.of(category.getId()),
                BigDecimal.valueOf(499.99),
                "url");
        Product product1 = testDataHelper.createProduct("PlayBox",
                "PlayBox",
                100,
                Set.of(category.getId()),
                BigDecimal.valueOf(499.99),
                "url");
        Product product2 = testDataHelper.createProduct("XBOX",
                "XBox",
                100,
                Set.of(category1.getId()),
                BigDecimal.valueOf(499.99),
                "url");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Product> products = productService.findAll(pageable);

        assertEquals(3, products.getContent().size());
        assertEquals(3, products.getTotalElements());
        assertTrue(products.getContent().stream().anyMatch(p -> p.getId().equals(product.getId())));
        assertTrue(products.getContent().stream().anyMatch(p -> p.getId().equals(product1.getId())));
        assertTrue(products.getContent().stream().anyMatch(p -> p.getId().equals(product2.getId())));
    }
}
