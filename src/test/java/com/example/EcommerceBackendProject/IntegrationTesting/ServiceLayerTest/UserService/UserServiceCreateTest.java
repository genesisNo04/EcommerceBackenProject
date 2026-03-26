package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.UserService;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.Role;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.AddressTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.UserTestFactory;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import com.example.EcommerceBackendProject.Mapper.UserMapper;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceCreateTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUser_success() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        Address address = AddressMapper.toEntity(addressRequestDTO);
        Address address1 = AddressMapper.toEntity(addressRequestDTO1);

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", addresses);
        User user = userService.createCustomerUser(userRequestDTO);
        assertNotNull(user.getId(), "Id is not generated");
        assertEquals("testuser", user.getUsername(), "Username does not match");
        assertTrue(passwordEncoder.matches("test123", user.getPassword()), "Password is not encrypted");
        assertEquals("testuser@gmail.com", user.getEmail(), "Email does not match");
        assertEquals("test", user.getFirstName(), "Firs tname does not match");
        assertEquals("user", user.getLastName(), "Last name does not match");
        assertEquals("+12345678981", user.getPhoneNumber(), "Phone Number does not match");
        assertEquals(addresses.size(), user.getAddresses().size(), "Address size does not match");
        assertTrue(user.getRoles().contains(Role.USER));
        assertFalse(user.getRoles().contains(Role.ADMIN));
        assertNotNull(user.getCart());
        assertEquals(user, user.getCart().getUser());
        assertTrue(user.getAddresses().stream().anyMatch(a ->
                a.getStreet().equals(address.getStreet()) &&
                a.getCity().equals(address.getCity()) &&
                a.getIsDefault().equals(true)));
        assertTrue(user.getAddresses().stream().anyMatch(a ->
                a.getStreet().equals(address1.getStreet()) &&
                        a.getCity().equals(address1.getCity()) &&
                        a.getIsDefault().equals(false)));
    }

    @Test
    void createUser_EmptyAddress() {
        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());
        User user = userService.createCustomerUser(userRequestDTO);
        assertNotNull(user.getId(), "Id is not generated");
        assertEquals("testuser", user.getUsername(), "Username does not match");
        assertTrue(passwordEncoder.matches("test123", user.getPassword()), "Password is not encrypted");
        assertEquals("testuser@gmail.com", user.getEmail(), "Email does not match");
        assertEquals("test", user.getFirstName(), "Firs tname does not match");
        assertEquals("user", user.getLastName(), "Last name does not match");
        assertEquals("+12345678981", user.getPhoneNumber(), "Phone Number does not match");
        assertTrue(user.getRoles().contains(Role.USER));
        assertFalse(user.getRoles().contains(Role.ADMIN));
        assertEquals(0, user.getAddresses().size());
        assertNotNull(user.getCart());
        assertEquals(user, user.getCart().getUser());
    }

    @Test
    void createUser_duplicate_username() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));
        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test","+12345678981", addresses);
        userService.createCustomerUser(userRequestDTO);

        UserRequestDTO userRequestDTO1 = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", "+12345678981", addresses);

        Exception ex = assertThrows(ResourceAlreadyExistsException.class, () -> userService.createCustomerUser(userRequestDTO));

        assertEquals("username or email is already exists.", ex.getMessage());
    }

    @Test
    void createUser_duplicate_username_dbConstraint() {
        AddressRequestDTO address = AddressTestFactory.createAddress(true);
        List<AddressRequestDTO> addresses = List.of(address);

        UserRequestDTO dto = UserTestFactory.createTestUser(
                "uniqueUser", "password", "unique@gmail.com",
                "first", "last", "+12345678981", addresses
        );

        userService.createCustomerUser(dto);

        User duplicate = UserMapper.toEntity(dto);
        duplicate.setPassword(passwordEncoder.encode("password"));

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.saveAndFlush(duplicate);
        });
    }

    @Test
    void createUser_duplicate_email() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", "+12345678981", addresses);
        userService.createCustomerUser(userRequestDTO);

        UserRequestDTO userRequestDTO1 = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", "+12345678981", addresses);

        Exception ex = assertThrows(ResourceAlreadyExistsException.class, () -> userService.createCustomerUser(userRequestDTO));

        assertEquals("username or email is already exists.", ex.getMessage());
    }

    @Test
    void createAdminUser_success() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        Address address = AddressMapper.toEntity(addressRequestDTO);
        Address address1 = AddressMapper.toEntity(addressRequestDTO1);

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", addresses);
        User user = userService.createAdmin(userRequestDTO);
        assertNotNull(user.getId(), "Id is not generated");
        assertEquals("testuser", user.getUsername(), "Username does not match");
        assertTrue(passwordEncoder.matches("test123", user.getPassword()), "Password is not encrypted");
        assertEquals("testuser@gmail.com", user.getEmail(), "Email does not match");
        assertEquals("test", user.getFirstName(), "Firs tname does not match");
        assertEquals("user", user.getLastName(), "Last name does not match");
        assertEquals("+12345678981", user.getPhoneNumber(), "Phone Number does not match");
        assertEquals(addresses.size(), user.getAddresses().size(), "Address size does not match");
        assertTrue(user.getRoles().contains(Role.ADMIN));
        assertFalse(user.getRoles().contains(Role.USER));
        assertTrue(user.getAddresses().stream().anyMatch(a ->
                a.getStreet().equals(address.getStreet()) &&
                        a.getCity().equals(address.getCity()) &&
                        a.getIsDefault().equals(true)));
        assertTrue(user.getAddresses().stream().anyMatch(a ->
                a.getStreet().equals(address1.getStreet()) &&
                        a.getCity().equals(address1.getCity()) &&
                        a.getIsDefault().equals(false)));
    }
}
