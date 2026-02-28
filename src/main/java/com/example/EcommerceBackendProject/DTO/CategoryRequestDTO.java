package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class CategoryRequestDTO {

    @NotBlank(message = "Category name cannot be blank")
    private String name;

    @NotBlank(message = "Category Description cannot be blank")
    private String description;

    private Set<Long> productIds = new HashSet<>();
}
