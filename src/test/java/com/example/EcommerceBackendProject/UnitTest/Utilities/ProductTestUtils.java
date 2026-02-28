package com.example.EcommerceBackendProject.UnitTest.Utilities;

import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.DTO.ProductUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Product;
import com.example.EcommerceBackendProject.Mapper.ProductMapper;

import java.math.BigDecimal;
import java.util.Set;

public class ProductTestUtils {

    public static Product createTestProduct(String productName, String description, BigDecimal price, Integer quantity, String imgUrl) {
        return ProductMapper.toEntity(new ProductRequestDTO(productName, description, quantity, Set.of(1L, 2L), price, imgUrl));
    }

    public static ProductRequestDTO createTestProductDTO(String productName, String description, BigDecimal price, Integer quantity, Set<Long> categoryIds, String imgUrl) {
        return new ProductRequestDTO(productName, description, quantity, categoryIds, price, imgUrl);
    }

    public static ProductUpdateRequestDTO createTestUpdateProductDTO(String productName, String description, BigDecimal price, Integer quantity, Set<Long> categoryIds, String imgUrl) {
        return new ProductUpdateRequestDTO(productName, description, quantity, categoryIds, price, imgUrl);
    }
}
