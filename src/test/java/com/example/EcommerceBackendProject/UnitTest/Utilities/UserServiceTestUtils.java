package com.example.EcommerceBackendProject.UnitTest.Utilities;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.Role;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import com.example.EcommerceBackendProject.Mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;

public class UserServiceTestUtils {

    public static AddressRequestDTO createAddressDTO(boolean isDefault) {

        return new AddressRequestDTO(
                "1234 Main st",
                "Sacramento",
                "CA",
                "USA",
                "12345",
                isDefault);
    }

    public static UserRequestDTO createUserDTO(String username, String password,
                                               String email, String firstName,
                                               String lastName, String phoneNumber,
                                               List<AddressRequestDTO> addresses) {

        return new UserRequestDTO(
                username,
                password,
                email,
                firstName,
                lastName,
                addresses,
                phoneNumber);
    }

    public static UserUpdateRequestDTO createUserUpdateDTO(String username,
                                                     String email, String firstName,
                                                     String lastName, String phoneNumber,
                                                     List<AddressRequestDTO> addresses) {

        return new UserUpdateRequestDTO(
                username,
                firstName,
                lastName,
                addresses,
                phoneNumber,
                email);
    }

    public static User createTestUser(String username, String password,
                                      String email, String firstName,
                                      String lastName, String phoneNumber,
                                      List<AddressRequestDTO> addresses) {
        UserRequestDTO userRequestDTO = createUserDTO(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                addresses);

        List<Address> outputAddress = new ArrayList<>(addresses.stream().map(AddressMapper::toEntity).toList());
        User user = UserMapper.toEntity(userRequestDTO);
        user.setPassword(password);
        user.setAddresses(outputAddress);
        return user;
    }
}
