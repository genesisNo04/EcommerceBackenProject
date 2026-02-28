package com.example.EcommerceBackendProject.ServiceLayerTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import com.example.EcommerceBackendProject.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceTestRefactoring {

    @Autowired
    private UserService userService;

    private AddressRequestDTO createAddress(boolean isDefault) {
        Random random = new Random();
        return new AddressRequestDTO(
                random.nextInt() + " Main st",
                "Sacramento",
                "CA",
                "USA",
                "12345",
                isDefault);
    }

    private UserUpdateRequestDTO createUpdateDTOTestUser(String username, String firstName, String lastName,
                                                   String email, String phoneNumber, List<AddressRequestDTO> addresses) {
        return new UserUpdateRequestDTO(
                username,
                firstName,
                lastName,
                addresses,
                phoneNumber,
                email);
    }

    private User createTestUser(String username, String password, String email, List<AddressRequestDTO> addresses) {
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                username,
                password,
                email,
                "test",
                "user",
                addresses,
                "+12345678981" );
        return userService.createCustomerUser(userRequestDTO);
    }

    @Test
    void createUser_success() {
        AddressRequestDTO addressRequestDTO = createAddress(true);
        AddressRequestDTO addressRequestDTO1 = createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        Address address = AddressMapper.toEntity(addressRequestDTO);
        Address address1 = AddressMapper.toEntity(addressRequestDTO1);

        User user = createTestUser("testuser", "test123", "testuser@gmail.com", addresses);

        assertNotNull(user.getId(), "Id is not generated");
        assertEquals("testuser", user.getUsername(), "Username does not match");
        assertNotEquals("test123", user.getPassword(), "Password is not encrypted");
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
        AddressRequestDTO addressRequestDTO = createAddress(true);
        AddressRequestDTO addressRequestDTO1 = createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        createTestUser("testuser1", "test123", "testuser1@gmail.com", addresses);

        Exception ex = assertThrows(ResourceAlreadyExistsException.class, () -> createTestUser("testuser1", "test123", "testuser11@gmail.com", addresses));

        assertEquals("username or email is already exists.", ex.getMessage());
    }

    @Test
    void createUser_duplicate_email() {
        AddressRequestDTO addressRequestDTO = createAddress(true);
        AddressRequestDTO addressRequestDTO1 = createAddress(false);
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(addressRequestDTO, addressRequestDTO1));

        createTestUser("testuser2", "test123", "testuser2@gmail.com", addresses);

        Exception ex = assertThrows(ResourceAlreadyExistsException.class, () -> createTestUser("testuser3", "test123", "testuser2@gmail.com", addresses));

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
        assertNotEquals(searchUser.getEmail(), user.getPassword(), "Password is not encrypted");
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
        assertNotEquals(searchUser.getEmail(), user.getPassword(), "Password is not encrypted");
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
        assertNotEquals(searchUser.getEmail(), user.getPassword(), "Password is not encrypted");
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
        assertEquals("test7", updateUser.getFirstName(), "First name does not match");
        assertEquals("last7", updateUser.getLastName(), "Last name does not match");
        assertEquals("+12345687465", updateUser.getPhoneNumber(), "Phone Number does not match");
        assertEquals(3, updateUser.getAddresses().size(), "Address size does not match");
    }
}
