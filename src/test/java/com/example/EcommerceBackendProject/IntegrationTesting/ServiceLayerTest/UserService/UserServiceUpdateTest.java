package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.UserService;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceUpdateTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void updateUser_success() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        AddressRequestDTO addressRequestDTO2 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", "+12345687465", addresses);
        User user = userService.createCustomerUser(userRequestDTO);

        UserUpdateRequestDTO userUpdateRequestDTO = UserTestFactory.createUpdateDTOTestUser("testuser7", "test7", "last7", "testuser7@gmail.com", "+12345687465", addresses);

        User updateUser = userService.updateUser(user.getId(), userUpdateRequestDTO);

        assertEquals("testuser7", updateUser.getUsername(), "Username does not match");
        assertEquals("testuser7@gmail.com", updateUser.getEmail(), "Email does not match");
        assertTrue(passwordEncoder.matches("test123", updateUser.getPassword()), "Password is not encrypted");
        assertEquals("test7", updateUser.getFirstName(), "First name does not match");
        assertEquals("last7", updateUser.getLastName(), "Last name does not match");
        assertEquals("+12345687465", updateUser.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, updateUser.getAddresses().size(), "Address size does not match");

        User persisted = userService.findById(user.getId());

        assertEquals("testuser7", persisted.getUsername(), "Username does not match");
        assertEquals("testuser7@gmail.com", persisted.getEmail(), "Email does not match");
        assertTrue(passwordEncoder.matches("test123", persisted.getPassword()), "Password is not encrypted");
        assertEquals("test7", persisted.getFirstName(), "First name does not match");
        assertEquals("last7", persisted.getLastName(), "Last name does not match");
        assertEquals("+12345687465", persisted.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, persisted.getAddresses().size(), "Address size does not match");
    }

    @Test
    void updateUser_duplicateUsername() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        AddressRequestDTO addressRequestDTO2 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser6", "test123", "testuser6@gmail.com", "user", "test", "+12345687465", addresses);
        User user = userService.createCustomerUser(userRequestDTO);

        UserRequestDTO userRequestDTO1 = UserTestFactory.createTestUser("testuser7", "test123", "testuser7@gmail.com", "user", "test", "+12345687465", addresses);
        User user1 = userService.createCustomerUser(userRequestDTO1);

        UserUpdateRequestDTO userUpdateRequestDTO = UserTestFactory.createUpdateDTOTestUser("testuser7", "test7", "last7", "testuser8@gmail.com", "+12345687465", addresses);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUser(user.getId(), userUpdateRequestDTO));

        assertEquals("Username is already in use", ex.getMessage());

        User persisted = userService.findById(user.getId());;

        assertEquals("testuser6", persisted.getUsername());
        assertEquals("testuser6@gmail.com", persisted.getEmail(), "Email does not match");
        assertTrue(passwordEncoder.matches("test123", persisted.getPassword()), "Password is not encrypted");
        assertEquals("user", persisted.getFirstName(), "First name does not match");
        assertEquals("test", persisted.getLastName(), "Last name does not match");
        assertEquals("+12345687465", persisted.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, persisted.getAddresses().size(), "Address size does not match");
    }

    @Test
    void updateUser_duplicateEmail() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        AddressRequestDTO addressRequestDTO2 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser6", "test123", "testuser6@gmail.com", "user", "test", "+12345687465", addresses);
        User user = userService.createCustomerUser(userRequestDTO);

        UserRequestDTO userRequestDTO1 = UserTestFactory.createTestUser("testuser7", "test123", "testuser7@gmail.com", "user", "test", "+12345687465", addresses);
        User user1 = userService.createCustomerUser(userRequestDTO1);

        UserUpdateRequestDTO userUpdateRequestDTO = UserTestFactory.createUpdateDTOTestUser("testuser8", "test7", "last7", "testuser7@gmail.com", "+12345687465", addresses);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUser(user.getId(), userUpdateRequestDTO));

        assertEquals("Email is already in use", ex.getMessage());

        User persisted = userService.findById(user.getId());;

        assertEquals("testuser6", persisted.getUsername());
        assertEquals("testuser6@gmail.com", persisted.getEmail(), "Email does not match");
        assertTrue(passwordEncoder.matches("test123", persisted.getPassword()), "Password is not encrypted");
        assertEquals("user", persisted.getFirstName(), "First name does not match");
        assertEquals("test", persisted.getLastName(), "Last name does not match");
        assertEquals("+12345687465", persisted.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, persisted.getAddresses().size(), "Address size does not match");
    }

    @Test
    void patchUser_success() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        AddressRequestDTO addressRequestDTO2 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", "+12345687465", addresses);
        User user = userService.createCustomerUser(userRequestDTO);

        UserUpdateRequestDTO userUpdateRequestDTO = UserTestFactory.createUpdateDTOTestUser("testuser7", "test7", "last7", "testuser7@gmail.com", "+12345687465", addresses);

        User updateUser = userService.patchUser(user.getId(), userUpdateRequestDTO);

        assertEquals("testuser7", updateUser.getUsername(), "Username does not match");
        assertEquals("testuser7@gmail.com", updateUser.getEmail(), "Email does not match");
        assertTrue(passwordEncoder.matches("test123", updateUser.getPassword()), "Password is not encrypted");
        assertEquals("test7", updateUser.getFirstName(), "First name does not match");
        assertEquals("last7", updateUser.getLastName(), "Last name does not match");
        assertEquals("+12345687465", updateUser.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, updateUser.getAddresses().size(), "Address size does not match");

        User persisted = userService.findById(user.getId());

        assertEquals("testuser7", persisted.getUsername(), "Username does not match");
        assertEquals("testuser7@gmail.com", persisted.getEmail(), "Email does not match");
        assertTrue(passwordEncoder.matches("test123", persisted.getPassword()), "Password is not encrypted");
        assertEquals("test7", persisted.getFirstName(), "First name does not match");
        assertEquals("last7", persisted.getLastName(), "Last name does not match");
        assertEquals("+12345687465", persisted.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, persisted.getAddresses().size(), "Address size does not match");
    }

    @Test
    void patchUser_success_notAllField() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        AddressRequestDTO addressRequestDTO2 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", "+12345678981", addresses);
        User user = userService.createCustomerUser(userRequestDTO);

        UserUpdateRequestDTO userUpdateRequestDTO = UserTestFactory.createUpdateDTOTestUser("testuser7", null, "last7", "testuser8@gmail.com", null, addresses);

        User updateUser = userService.patchUser(user.getId(), userUpdateRequestDTO);

        assertEquals("testuser7", updateUser.getUsername(), "Username does not match");
        assertEquals("testuser8@gmail.com", updateUser.getEmail(), "Email does not match");
        assertTrue(passwordEncoder.matches("test123", updateUser.getPassword()), "Password is not encrypted");
        assertEquals("user", updateUser.getFirstName(), "First name does not match");
        assertEquals("last7", updateUser.getLastName(), "Last name does not match");
        assertEquals("+12345678981", updateUser.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, updateUser.getAddresses().size(), "Address size does not match");

        User persisted = userService.findById(user.getId());

        assertEquals("testuser7", persisted.getUsername(), "Username does not match");
        assertEquals("testuser8@gmail.com", persisted.getEmail(), "Email does not match");
        assertTrue(passwordEncoder.matches("test123", persisted.getPassword()), "Password is not encrypted");
        assertEquals("user", persisted.getFirstName(), "First name does not match");
        assertEquals("last7", persisted.getLastName(), "Last name does not match");
        assertEquals("+12345678981", persisted.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, persisted.getAddresses().size(), "Address size does not match");
    }

    @Test
    void patchUser_success_allFieldNull() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        AddressRequestDTO addressRequestDTO2 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", "+12345678981", addresses);
        User user = userService.createCustomerUser(userRequestDTO);

        UserUpdateRequestDTO userUpdateRequestDTO = UserTestFactory.createUpdateDTOTestUser(null, null, null, null, null, null);

        User updateUser = userService.patchUser(user.getId(), userUpdateRequestDTO);

        assertEquals("testuser1", updateUser.getUsername(), "Username does not match");
        assertEquals("testuser1@gmail.com", updateUser.getEmail(), "Email does not match");
        assertTrue(passwordEncoder.matches("test123", updateUser.getPassword()), "Password is not encrypted");
        assertEquals("user", updateUser.getFirstName(), "First name does not match");
        assertEquals("test", updateUser.getLastName(), "Last name does not match");
        assertEquals("+12345678981", updateUser.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, updateUser.getAddresses().size(), "Address size does not match");

        User persisted = userService.findById(user.getId());

        assertEquals("testuser1", persisted.getUsername(), "Username does not match");
        assertEquals("testuser1@gmail.com", persisted.getEmail(), "Email does not match");
        assertTrue(passwordEncoder.matches("test123", persisted.getPassword()), "Password is not encrypted");
        assertEquals("user", persisted.getFirstName(), "First name does not match");
        assertEquals("test", persisted.getLastName(), "Last name does not match");
        assertEquals("+12345678981", persisted.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, persisted.getAddresses().size(), "Address size does not match");
    }

    @Test
    void patchUser_duplicateUsername() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        AddressRequestDTO addressRequestDTO2 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser6", "test123", "testuser6@gmail.com", "user", "test", "+12345678981", addresses);
        User user = userService.createCustomerUser(userRequestDTO);

        UserRequestDTO userRequestDTO1 = UserTestFactory.createTestUser("testuser7", "test123", "testuser7@gmail.com", "user", "test", "+12345678981", addresses);
        User user1 = userService.createCustomerUser(userRequestDTO1);

        UserUpdateRequestDTO userUpdateRequestDTO = UserTestFactory.createUpdateDTOTestUser("testuser7", "test7", "last7", "testuser8@gmail.com", "+12345687465", addresses);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.patchUser(user.getId(), userUpdateRequestDTO));

        assertEquals("Username is already in use", ex.getMessage());

        User persisted = userService.findById(user.getId());;

        assertEquals("testuser6", persisted.getUsername());
        assertEquals("testuser6@gmail.com", persisted.getEmail(), "Email does not match");
        assertTrue(passwordEncoder.matches("test123", persisted.getPassword()), "Password is not encrypted");
        assertEquals("user", persisted.getFirstName(), "First name does not match");
        assertEquals("test", persisted.getLastName(), "Last name does not match");
        assertEquals("+12345678981", persisted.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, persisted.getAddresses().size(), "Address size does not match");
    }

    @Test
    void patchUser_duplicateEmail() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        AddressRequestDTO addressRequestDTO2 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser6", "test123", "testuser6@gmail.com", "user", "test", "+12345678981", addresses);
        User user = userService.createCustomerUser(userRequestDTO);

        UserRequestDTO userRequestDTO1 = UserTestFactory.createTestUser("testuser7", "test123", "testuser7@gmail.com", "user", "test", "+12345678981", addresses);
        User user1 = userService.createCustomerUser(userRequestDTO1);

        UserUpdateRequestDTO userUpdateRequestDTO = UserTestFactory.createUpdateDTOTestUser("testuser8", "test7", "last7", "testuser7@gmail.com", "+12345687465", addresses);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.patchUser(user.getId(), userUpdateRequestDTO));

        assertEquals("Email is already in use", ex.getMessage());

        User persisted = userService.findById(user.getId());;

        assertEquals("testuser6", persisted.getUsername());
        assertEquals("testuser6@gmail.com", persisted.getEmail(), "Email does not match");
        assertTrue(passwordEncoder.matches("test123", persisted.getPassword()), "Password is not match");
        assertEquals("user", persisted.getFirstName(), "First name does not match");
        assertEquals("test", persisted.getLastName(), "Last name does not match");
        assertEquals("+12345678981", persisted.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, persisted.getAddresses().size(), "Address size does not match");
    }

    @Test
    void changePassword() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        AddressRequestDTO addressRequestDTO2 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser6", "test123", "testuser6@gmail.com", "user", "test", "+12345678981", addresses);
        User user = userService.createCustomerUser(userRequestDTO);

        userService.changePassword(user.getId(), "test1234");

        assertTrue(passwordEncoder.matches("test1234", user.getPassword()), "Password is not match");
    }

    @Test
    void changePassword_noUserFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> userService.changePassword(999L, "test1234"));

        assertEquals("User not found", ex.getMessage());
    }

}
