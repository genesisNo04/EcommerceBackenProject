package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class ProductUpdateRequestDTO {

    private String productName;

    private String description;

    @Min(1)
    private Integer stockQuantity;

    private Set<Long> categoriesId;

    @Min(0)
    private BigDecimal price;

    private String imageUrl;
}
