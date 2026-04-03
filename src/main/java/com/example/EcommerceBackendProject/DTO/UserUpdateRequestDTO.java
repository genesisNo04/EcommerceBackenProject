package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserUpdateRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotEmpty
    @Valid
    private List<AddressRequestDTO> address;

    @NotBlank
    private String phoneNumber;

    @Email
    @NotBlank
    private String email;
}
