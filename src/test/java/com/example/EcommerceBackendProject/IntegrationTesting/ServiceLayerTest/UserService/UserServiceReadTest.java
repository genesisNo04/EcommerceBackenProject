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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceReadTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void searchUser_byId() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", "+12345678981", addresses);
        User user = userService.createCustomerUser(userRequestDTO);

        User searchUser = userService.findById(user.getId());
        assertEquals(user.getUsername(), searchUser.getUsername(), "Username does not match");
        assertTrue(passwordEncoder.matches("test123", user.getPassword()), "Password is not encrypted");
        assertEquals(searchUser.getEmail(), user.getEmail(), "Email does not match");
        assertEquals(searchUser.getFirstName(), user.getFirstName(), "First name does not match");
        assertEquals(searchUser.getLastName(), user.getLastName(), "Last name does not match");
        assertEquals(searchUser.getPhoneNumber(), user.getPhoneNumber(), "Phone Number does not match");
        assertEquals(searchUser.getAddresses().size(), user.getAddresses().size(), "Address size does not match");

    }

    @Test
    void searchUser_byId_idNotFound() {
        Exception ex = assertThrows(NoResourceFoundException.class, () -> userService.findById(123455648L));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void searchUser_byUsername() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", "+12345678981", addresses);
        User user = userService.createCustomerUser(userRequestDTO);

        User searchUser = userService.findByUsername(user.getUsername());
        assertEquals(user.getUsername(), searchUser.getUsername(), "Username does not match");
        assertTrue(passwordEncoder.matches("test123", user.getPassword()), "Password is not encrypted");
        assertEquals(searchUser.getEmail(), user.getEmail(), "Email does not match");
        assertEquals(searchUser.getFirstName(), user.getFirstName(), "First name does not match");
        assertEquals(searchUser.getLastName(), user.getLastName(), "Last name does not match");
        assertEquals(searchUser.getPhoneNumber(), user.getPhoneNumber(), "Phone Number does not match");
        assertEquals(searchUser.getAddresses().size(), user.getAddresses().size(), "Address size does not match");

    }

    @Test
    void searchUser_byUsername_userNotFound() {
        Exception ex = assertThrows(NoResourceFoundException.class, () -> userService.findByUsername("TestUserName"));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void searchUser_byEmail() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", "+12345678981", addresses);
        User user = userService.createCustomerUser(userRequestDTO);

        User searchUser = userService.findByEmail(user.getEmail());
        assertEquals(user.getUsername(), searchUser.getUsername(), "Username does not match");
        assertTrue(passwordEncoder.matches("test123", user.getPassword()), "Password is not encrypted");
        assertEquals(searchUser.getEmail(), user.getEmail(), "Email does not match");
        assertEquals(searchUser.getFirstName(), user.getFirstName(), "First name does not match");
        assertEquals(searchUser.getLastName(), user.getLastName(), "Last name does not match");
        assertEquals(searchUser.getPhoneNumber(), user.getPhoneNumber(), "Phone Number does not match");
        assertEquals(searchUser.getAddresses().size(), user.getAddresses().size(), "Address size does not match");

    }

    @Test
    void searchUser_byEmail_userNotFound() {
        Exception ex = assertThrows(NoResourceFoundException.class, () -> userService.findByEmail("TestUserEmail@gmail.com"));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void findAll() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", "+12345678981", addresses);
        User user = userService.createCustomerUser(userRequestDTO);
        UserRequestDTO userRequestDTO1 = UserTestFactory.createTestUser("testuser2", "test123", "testuser2@gmail.com", "user", "test", "+12345678981", addresses);
        User user1 = userService.createCustomerUser(userRequestDTO1);
        UserRequestDTO userRequestDTO2 = UserTestFactory.createTestUser("testuser3", "test123", "testuser3@gmail.com", "user", "test", "+12345678981", addresses);
        User user2 = userService.createCustomerUser(userRequestDTO2);
        UserRequestDTO userRequestDTO3 = UserTestFactory.createTestUser("testuser4", "test123", "testuser4@gmail.com", "user", "test", "+12345678981", addresses);
        User user3 = userService.createCustomerUser(userRequestDTO3);
        List<User> userList = List.of(user, user1, user2, user3);

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> users = userService.findAll(pageable);

        assertEquals(userList, users.getContent());
        assertEquals(4, users.getTotalElements());
        assertEquals(4, users.getContent().size());
        assertTrue(users.getContent().stream().anyMatch(u -> u.getUsername().equals("testuser1")));
    }

    @Test
    void findByIdForAdmin() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", "+12345678981", addresses);
        User user = userService.createCustomerUser(userRequestDTO);
        UserRequestDTO userRequestDTO1 = UserTestFactory.createTestUser("testuser2", "test123", "testuser2@gmail.com", "user", "test", "+12345678981", addresses);
        User user1 = userService.createCustomerUser(userRequestDTO1);
        Pageable pageable = PageRequest.of(0, 1);

        Page<User> searchUser = userService.findById(user.getId(), pageable);

        assertEquals(1, searchUser.getContent().size());
        assertEquals(1, searchUser.getTotalElements());
        assertEquals(user.getId(), searchUser.getContent().get(0).getId());
        assertNotSame(user1, searchUser.getContent().get(0));
    }

    @Test
    void findByIdForAdmin_noUserFound() {
        Pageable pageable = PageRequest.of(0, 1);
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> userService.findById(999L, pageable));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void findByUsernameForAdmin() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", "+12345678981", addresses);
        User user = userService.createCustomerUser(userRequestDTO);
        UserRequestDTO userRequestDTO1 = UserTestFactory.createTestUser("testuser2", "test123", "testuser2@gmail.com", "user", "test", "+12345678981", addresses);
        User user1 = userService.createCustomerUser(userRequestDTO1);
        Pageable pageable = PageRequest.of(0, 1);

        Page<User> searchUser = userService.findByUsername("testuser1", pageable);

        assertEquals(1, searchUser.getContent().size());
        assertEquals(1, searchUser.getTotalElements());
        assertEquals(user.getId(), searchUser.getContent().get(0).getId());
        assertNotSame(user1, searchUser.getContent().get(0));
    }

    @Test
    void findByUsernameForAdmin_noUserFound() {
        Pageable pageable = PageRequest.of(0, 1);
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> userService.findByUsername("testuser1", pageable));

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void findByEmailForAdmin() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", "+12345678981", addresses);
        User user = userService.createCustomerUser(userRequestDTO);
        UserRequestDTO userRequestDTO1 = UserTestFactory.createTestUser("testuser2", "test123", "testuser2@gmail.com", "user", "test", "+12345678981", addresses);
        User user1 = userService.createCustomerUser(userRequestDTO1);
        Pageable pageable = PageRequest.of(0, 1);

        Page<User> searchUser = userService.findByEmail("testuser1@gmail.com", pageable);

        assertEquals(1, searchUser.getContent().size());
        assertEquals(1, searchUser.getTotalElements());
        assertEquals(user.getId(), searchUser.getContent().get(0).getId());
        assertNotSame(user1, searchUser.getContent().get(0));
    }

    @Test
    void findByEmailForAdmin_noUserFound() {
        Pageable pageable = PageRequest.of(0, 1);
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> userService.findByEmail("testuser2@gmail.com", pageable));

        assertEquals("User not found", ex.getMessage());
    }
}
