package com.example.EcommerceBackendProject.DTO;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserUpdateRequestDTO {

    private String username;

    private String firstName;

    private String lastName;

    private List<AddressRequestDTO> address;

    private String phoneNumber;

    @Email
    private String email;
}
