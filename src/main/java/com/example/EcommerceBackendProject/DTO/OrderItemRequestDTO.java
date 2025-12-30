package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequestDTO {

    @NotNull
    private Long productId;

    @Min(1)
    private Integer quantity;
}
