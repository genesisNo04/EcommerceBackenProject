package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.AddressTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.UserTestFactory;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
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
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createUser_success() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        Address address = AddressMapper.toEntity(addressRequestDTO);
        Address address1 = AddressMapper.toEntity(addressRequestDTO1);

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", addresses);
        User user = userService.createCustomerUser(userRequestDTO);
        assertNotNull(user.getId(), "Id is not generated");
        assertEquals("testuser", user.getUsername(), "Username does not match");
        assertTrue(passwordEncoder.matches("test123", user.getPassword()), "Password is not encrypted");
        assertEquals("testuser@gmail.com", user.getEmail(), "Email does not match");
        assertEquals("test", user.getFirstName(), "Firs tname does not match");
        assertEquals("user", user.getLastName(), "Last name does not match");
        assertEquals("+12345678981", user.getPhoneNumber(), "Phone Number does not match");
        assertEquals(addresses.size(), user.getAddresses().size(), "Address size does not match");
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
    void createUser_duplicate_username() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));
        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", addresses);
        userService.createCustomerUser(userRequestDTO);

        UserRequestDTO userRequestDTO1 = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", addresses);

        Exception ex = assertThrows(ResourceAlreadyExistsException.class, () -> userService.createCustomerUser(userRequestDTO));

        assertEquals("username or email is already exists.", ex.getMessage());
    }

    @Test
    void createUser_duplicate_email() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress(true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        UserRequestDTO userRequestDTO = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", addresses);
        userService.createCustomerUser(userRequestDTO);

        UserRequestDTO userRequestDTO1 = UserTestFactory.createTestUser("testuser1", "test123", "testuser1@gmail.com", "user", "test", addresses);

        Exception ex = assertThrows(ResourceAlreadyExistsException.class, () -> userService.createCustomerUser(userRequestDTO));

        assertEquals("username or email is already exists.", ex.getMessage());
    }

    @Test
    void searchUser_byId() {
        AddressRequestDTO addressRequestDTO = createAddress(true);
        AddressRequestDTO addressRequestDTO1 = createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        User user = createTestUser("testuser3", "test123", "testuser3@gmail.com", addresses);

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
        AddressRequestDTO addressRequestDTO = createAddress(true);
        AddressRequestDTO addressRequestDTO1 = createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        User user = createTestUser("testuser4", "test123", "testuser4@gmail.com", addresses);

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
        AddressRequestDTO addressRequestDTO = createAddress(true);
        AddressRequestDTO addressRequestDTO1 = createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        User user = createTestUser("testuser5", "test123", "testuser5@gmail.com", addresses);

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
    void updateUser_success() {
        AddressRequestDTO addressRequestDTO = createAddress(true);
        AddressRequestDTO addressRequestDTO1 = createAddress(false);
        AddressRequestDTO addressRequestDTO2 = createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2));

        Address address = AddressMapper.toEntity(addressRequestDTO);
        Address address1 = AddressMapper.toEntity(addressRequestDTO1);

        User user = createTestUser("testuser6", "test123", "testuser6@gmail.com", addresses);

        User updateUser = userService.updateUser(user.getId(), createUpdateDTOTestUser("testuser7", "test7", "last7", "testuser7@gmail.com", "+12345687465", addresses));

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
        AddressRequestDTO addressRequestDTO = createAddress(true);
        AddressRequestDTO addressRequestDTO1 = createAddress(false);
        AddressRequestDTO addressRequestDTO2 = createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2));

        Address address = AddressMapper.toEntity(addressRequestDTO);
        Address address1 = AddressMapper.toEntity(addressRequestDTO1);

        User user = createTestUser("testuser6", "test123", "testuser6@gmail.com", addresses);
        User user1 = createTestUser("testuser7", "test123", "testuser7@gmail.com", addresses);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUser(user.getId(), createUpdateDTOTestUser("testuser7", "test7", "last7", "testuser8@gmail.com", "+12345687465", addresses)));

        assertEquals("Username is already in use", ex.getMessage());

        User persisted = userService.findById(user.getId());;

        assertEquals("testuser6", persisted.getUsername());
        assertEquals("testuser6@gmail.com", persisted.getEmail(), "Email does not match");
        assertTrue(passwordEncoder.matches("test123", persisted.getPassword()), "Password is not encrypted");
        assertEquals("test", persisted.getFirstName(), "First name does not match");
        assertEquals("user", persisted.getLastName(), "Last name does not match");
        assertEquals("+12345678981", persisted.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, persisted.getAddresses().size(), "Address size does not match");
    }

    @Test
    void updateUser_duplicateEmail() {
        AddressRequestDTO addressRequestDTO = createAddress(true);
        AddressRequestDTO addressRequestDTO1 = createAddress(false);
        AddressRequestDTO addressRequestDTO2 = createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2));

        Address address = AddressMapper.toEntity(addressRequestDTO);
        Address address1 = AddressMapper.toEntity(addressRequestDTO1);

        User user = createTestUser("testuser6", "test123", "testuser6@gmail.com", addresses);
        User user1 = createTestUser("testuser7", "test123", "testuser7@gmail.com", addresses);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateUser(user.getId(), createUpdateDTOTestUser("testuser8", "test", "last", "testuser7@gmail.com", "+12345687465", addresses)));

        assertEquals("Email is already in use", ex.getMessage());

        User persisted = userService.findById(user.getId());;

        assertEquals("testuser6", persisted.getUsername());
        assertEquals("testuser6@gmail.com", persisted.getEmail(), "Email does not match");
        assertTrue(passwordEncoder.matches("test123", persisted.getPassword()), "Password is not encrypted");
        assertEquals("test", persisted.getFirstName(), "First name does not match");
        assertEquals("user", persisted.getLastName(), "Last name does not match");
        assertEquals("+12345678981", persisted.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, persisted.getAddresses().size(), "Address size does not match");
    }
}
