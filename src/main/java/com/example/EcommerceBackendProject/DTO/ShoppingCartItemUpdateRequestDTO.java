package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShoppingCartItemUpdateRequestDTO {
    @NotNull
    @Min(1)
    private Integer quantity;
}
