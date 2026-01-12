package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserUpdateRequestDTO {

    private String firstName;

    private String lastName;

    private List<AddressRequestDTO> address;

    private String phoneNumber;

    @Email
    private String email;
}
