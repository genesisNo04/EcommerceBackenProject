package com.example.EcommerceBackendProject.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserResponseDTO {

    private Long userId;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private List<AddressResponseDTO> address;

    private String phoneNumber;
}
