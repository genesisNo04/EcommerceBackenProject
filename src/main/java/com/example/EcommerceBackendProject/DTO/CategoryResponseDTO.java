package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class CategoryResponseDTO {

    private Long id;

    private String name;

    private String description;

    private Set<Long> productIds = new HashSet<>();
}
