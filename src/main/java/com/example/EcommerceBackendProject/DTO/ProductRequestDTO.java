package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
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
    private Set<Long> categoriesId;

    @NotNull
    @Min(0)
    private BigDecimal price;

    @NotNull
    private String imageUrl;
}
