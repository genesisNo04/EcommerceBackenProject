package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderRequestDTO {

    @NotEmpty
    private List<OrderItemRequestDTO> orderItems;

}
