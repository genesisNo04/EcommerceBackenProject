package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserRequestDTO {

    @NotNull
    private String username;

    @NotNull
    private String password;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    @NotEmpty
    private List<AddressRequestDTO> address;

    @NotNull
    private String phoneNumber;
}
