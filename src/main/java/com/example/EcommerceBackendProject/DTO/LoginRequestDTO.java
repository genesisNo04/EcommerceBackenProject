package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    private String identifier;

    private String password;
}
