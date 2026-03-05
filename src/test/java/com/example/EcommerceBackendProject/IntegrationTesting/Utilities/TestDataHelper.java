package com.example.EcommerceBackendProject.IntegrationTesting.Utilities;

import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestDataHelper {

    @Autowired
    private UserService userService;

    public User createUser() {
        UserRequestDTO dto = UserTestFactory.createTestUser(
                "testuser",
                "test123",
                "testuser@gmail.com",
                "test",
                "user",
                "+12345678981",
                List.of()
        );
        return userService.createCustomerUser(dto);
    }
}
