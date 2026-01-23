package com.example.EcommerceBackendProject.DTO;

import com.example.EcommerceBackendProject.Entity.Category;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class ProductResponseDTO {

    private Long productId;

    private String productName;

    private String description;

    private Integer stockQuantity;

    private BigDecimal price;

    private Set<Long> categoriesId;

    private String imageUrl;

    private LocalDateTime createdAt;
}
