package com.example.EcommerceBackendProject.DTO;

import com.example.EcommerceBackendProject.Entity.Category;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
public class ProductRequestDTO {

    @NotBlank
    private String productName;

    @NotBlank
    private String description;

    @NotNull
    @Min(1)
    private Integer stockQuantity;

    @NotEmpty
    @NotNull
    private Set<CategoryRequestDTO> categories;

    @NotNull
    @Min(0)
    private BigDecimal price;

    @NotNull
    private String imageUrl;
}
