package com.example.EcommerceBackendProject.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class CategoryUpdateRequestDTO {

    private String name;

    private String description;

    private Set<Long> productIds;
}
