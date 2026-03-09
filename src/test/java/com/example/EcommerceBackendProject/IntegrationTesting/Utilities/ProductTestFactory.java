package com.example.EcommerceBackendProject.IntegrationTesting.Utilities;

import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;

import java.math.BigDecimal;
import java.util.Set;

public class ProductTestFactory {

    public static ProductRequestDTO createProductDTO(String productName,
                                                     String description,
                                                     Integer stockQuantity,
                                                     Set<Long> categoriesId,
                                                     BigDecimal price,
                                                     String imageUrl) {
        return new ProductRequestDTO(productName,
                description,
                stockQuantity,
                categoriesId,
                price,
                imageUrl);
    }
}
