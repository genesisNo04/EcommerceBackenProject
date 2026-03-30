package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginRequestDTO {

    @NotNull
    @NotBlank(message = "username or email is required")
    private String identifier;

    @NotNull
    @NotBlank(message = "password is required")
    private String password;
}
