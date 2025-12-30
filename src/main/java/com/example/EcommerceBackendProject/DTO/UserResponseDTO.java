package com.example.EcommerceBackendProject.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    private Long userId;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private AddressResponseDTO address;

    private String phoneNumber;
}
