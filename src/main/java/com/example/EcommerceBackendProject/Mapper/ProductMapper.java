package com.example.EcommerceBackendProject.Mapper;

import com.example.EcommerceBackendProject.DTO.ProductRequestDTO;
import com.example.EcommerceBackendProject.DTO.ProductResponseDTO;
import com.example.EcommerceBackendProject.Entity.Product;

import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductResponseDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setProductId(product.getId());
        productResponseDTO.setProductName(product.getProductName());
        productResponseDTO.setDescription(product.getDescription());
        productResponseDTO.setStockQuantity(product.getStockQuantity());
        productResponseDTO.setImageUrl(product.getImageUrl());
        productResponseDTO.setCreatedAt(product.getCreatedAt());

        if (product.getCategories() != null) {
            productResponseDTO.setCategories(product.getCategories()
                    .stream()
                    .map(CategoryMapper::toDTO)
                    .collect(Collectors.toSet()));
        }

        return productResponseDTO;
    }

    public static Product toEntity(ProductRequestDTO productRequestDTO) {
        Product product = new Product();
        product.setProductName(productRequestDTO.getProductName());
        product.setDescription(productRequestDTO.getDescription());
        product.setStockQuantity(productRequestDTO.getStockQuantity());
        product.setPrice(productRequestDTO.getPrice());
        product.setImageUrl(productRequestDTO.getImageUrl());
        return product;
    }
}
