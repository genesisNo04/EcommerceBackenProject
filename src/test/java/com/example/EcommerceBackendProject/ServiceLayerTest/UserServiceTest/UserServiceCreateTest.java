package com.example.EcommerceBackendProject.ServiceLayerTest.UserServiceTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import com.example.EcommerceBackendProject.Mapper.UserMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceCreateTest {

    public static AddressRequestDTO createAddress(boolean isDefault) {

        return new AddressRequestDTO(
                "1234 Main st",
                "Sacramento",
                "CA",
                "USA",
                "12345",
                isDefault);
    }

    public static User createTestUser(String username, String password, String email, List<AddressRequestDTO> addresses) {
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                username,
                password,
                email,
                "test",
                "user",
                addresses,
                "+12345678981");
        List<Address> outputAddress = new ArrayList<>(addresses.stream().map(AddressMapper::toEntity).toList());
        User user = UserMapper.toEntity(userRequestDTO);
        user.setPassword(password);
        user.setAddresses(outputAddress);
        return user;
    }
}
