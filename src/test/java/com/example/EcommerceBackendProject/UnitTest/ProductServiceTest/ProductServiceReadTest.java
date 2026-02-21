package com.example.EcommerceBackendProject.UnitTest.ProductServiceTest;

import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.CategoryTestUtils.createTestCategory;
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

    @Test
    void findProductPrice() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");

        when(productRepository.findPriceById(1L)).thenReturn(Optional.of(product.getPrice()));

        BigDecimal price = productService.findProductPrice(1L);

        assertEquals(BigDecimal.valueOf(499.99), price);
        verify(productRepository).findPriceById(1L);
    }

    @Test
    void findProductPrice_productNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.findProductPrice(1L));

        assertEquals("No product found", ex.getMessage());
        verify(productRepository).findPriceById(1L);
    }

    @Test
    void findProductByKeyword() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        Product product1 = createTestProduct("PLAYBOY", "PlayBoy", BigDecimal.valueOf(199.99), 100, "testurl");
        List<Product> productList = List.of(product, product1);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findByProductNameContainingIgnoreCase("PLAY", pageable)).thenReturn(productPage);

        Page<Product> result = productService.findProductByKeyword("PLAY", pageable);

        assertEquals(productList.size(), result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.getContent().stream().anyMatch(p -> p.getProductName().contains("PLAY")));
        verify(productRepository).findByProductNameContainingIgnoreCase("PLAY", pageable);
    }

    @Test
    void findProductByKeyword_noProductFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findByProductNameContainingIgnoreCase("PLAY", pageable)).thenReturn(Page.empty());

        Page<Product> result = productService.findProductByKeyword("PLAY", pageable);

        assertEquals(0, result.getTotalElements());
        verify(productRepository).findByProductNameContainingIgnoreCase("PLAY", pageable);
    }

    @Test
    void findProductByCategory() {
        Category category = createTestCategory("ENTERTAINMENT", "entertainment");
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        product.setCategories(Set.of(category));
        Product product1 = createTestProduct("PLAYBOY", "PlayBoy", BigDecimal.valueOf(199.99), 100, "testurl");
        product1.setCategories(Set.of(category));
        List<Product> productList = List.of(product, product1);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findByCategories(category, pageable)).thenReturn(productPage);

        Page<Product> result = productService.findProductByCategory(category, pageable);

        assertEquals(productList.size(), result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.getContent().stream().anyMatch(p -> p.getCategories().contains(category)));
        verify(productRepository).findByCategories(category, pageable);
    }

    @Test
    void findProductByCategory_noProductFound() {
        Category category = createTestCategory("ENTERTAINMENT", "entertainment");
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findByCategories(category, pageable)).thenReturn(Page.empty());

        Page<Product> result = productService.findProductByCategory(category, pageable);

        assertEquals(0, result.getTotalElements());
        verify(productRepository).findByCategories(category, pageable);
    }

    @Test
    void findAll() {
        Product product = createTestProduct("PS5", "Playstation", BigDecimal.valueOf(499.99), 100, "testurl");
        Product product1 = createTestProduct("PLAYBOY", "PlayBoy", BigDecimal.valueOf(199.99), 100, "testurl");
        Product product2 = createTestProduct("XBOX", "PlayBoy", BigDecimal.valueOf(199.99), 100, "testurl");
        Product product3 = createTestProduct("NINTENDO", "PlayBoy", BigDecimal.valueOf(199.99), 100, "testurl");
        List<Product> productList = List.of(product, product1, product2, product3);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        Page<Product> result = productService.findAll(pageable);

        assertEquals(productList.size(), result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(productList, result.getContent());
        verify(productRepository).findAll(pageable);
    }

}
