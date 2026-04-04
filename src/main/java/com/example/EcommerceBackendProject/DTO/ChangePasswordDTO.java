package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChangePasswordDTO {

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;
}
