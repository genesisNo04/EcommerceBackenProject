package com.example.EcommerceBackendProject.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class CategoryUpdateRequestDTO {

    private String name;

    private String description;

    private Set<Long> productIds = new HashSet<>();
}
