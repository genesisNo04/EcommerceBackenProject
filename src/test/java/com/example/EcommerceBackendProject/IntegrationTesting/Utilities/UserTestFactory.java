package com.example.EcommerceBackendProject.IntegrationTesting.Utilities;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.User;

import java.util.List;

public final class UserTestFactory {

    public static UserUpdateRequestDTO createUpdateDTOTestUser(String username, String firstName, String lastName,
                                                         String email, String phoneNumber, List<AddressRequestDTO> addresses) {
        return new UserUpdateRequestDTO(
                username,
                firstName,
                lastName,
                addresses,
                phoneNumber,
                email);
    }

    public static UserRequestDTO createTestUser(String username, String password, String email, String firstName, String lastName, List<AddressRequestDTO> addresses) {
        return new UserRequestDTO(
                username,
                password,
                email,
                firstName,
                lastName,
                addresses,
                "+12345678981" );
    }
}
