package com.example.EcommerceBackendProject.UnitTest.ProductServiceTest;

import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.ProductTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceDeleteTest extends BaseProductServiceTest{

    @Test
    void deleteProduct() {
        Product product = createTestProduct("XBOX", "xbox", BigDecimal.valueOf(499.99), 50, "testurl");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        InOrder inOrder = inOrder(shoppingCartItemRepository, productRepository);
        verify(productRepository).findById(1L);
        inOrder.verify(shoppingCartItemRepository).deleteByProductId(1L);
        inOrder.verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_productNotFound() {

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> productService.deleteProduct(1L));

        assertEquals("No product found", ex.getMessage());
        verify(shoppingCartItemRepository, never()).deleteByProductId(1L);
        verify(productRepository, never()).delete(any(Product.class));
    }
}
