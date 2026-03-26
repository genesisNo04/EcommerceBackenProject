package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.UserService;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.AddressTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.UserTestFactory;
import com.example.EcommerceBackendProject.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceDeleteTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void deleteUser() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", addresses);
        User user = userService.createCustomerUser(userRequestDTO);

        User searchUser = userService.findById(user.getId());
        assertEquals(user.getId(), searchUser.getId());

        userService.deleteUser(user.getId());

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> userService.findById(user.getId()));
        NoResourceFoundException ex1 = assertThrows(NoResourceFoundException.class, () -> userService.findByUsername("testuser"));

        assertEquals("User not found", ex.getMessage());
        assertEquals("User not found", ex1.getMessage());
    }

    @Test
    void deleteUser_userNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> userService.deleteUser(999L));

        assertEquals("User not found", ex.getMessage());
    }
}
