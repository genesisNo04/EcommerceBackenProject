package com.example.EcommerceBackendProject.ServiceLayerTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import com.example.EcommerceBackendProject.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;
    @Test
    void createUser_success() {
        AddressRequestDTO addressRequestDTO =
                new AddressRequestDTO(
                        "123 Main st",
                        "Sacramento",
                        "CA",
                        "USA",
                        "12345",
                        true );

        AddressRequestDTO addressRequestDTO1 =
                new AddressRequestDTO(
                        "1234 Main st",
                        "Sacramento",
                        "CA",
                        "USA",
                        "12445",
                        false );

        Address address = AddressMapper.toEntity(addressRequestDTO);
        Address address1 = AddressMapper.toEntity(addressRequestDTO1);

        UserRequestDTO userRequestDTO = new UserRequestDTO(
                "testuser",
                "testuser",
                "testuser@gmail.com",
                "test",
                "user",
                new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1)),
                "+12345678981" );
        User user = userService.createCustomerUser(userRequestDTO);

        assertNotNull(user.getId(), "Id is not generated");
        assertEquals("testuser", user.getUsername(), "Username does not match");
        assertNotEquals("testuser", user.getPassword(), "Password is not encrypted");
        assertEquals("testuser@gmail.com", user.getEmail(), "Email does not match");
        assertEquals("test", user.getFirstName(), "Firstname does not match");
        assertEquals("user", user.getLastName(), "Lastname does not match");
        assertEquals("+12345678981", user.getPhoneNumber(), "PhomeNumber does not match");
//        assertTrue(user.getAddresses().contains(address));
    }
}
