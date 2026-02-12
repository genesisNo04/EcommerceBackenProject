package com.example.EcommerceBackendProject.UnitTest.UserServiceTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.example.EcommerceBackendProject.UnitTest.UserServiceTest.UserServiceTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceUpdateTest extends BaseUserServiceTest{

    @Test
    void updateCustomerUser() {
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddressDTO(true), createAddressDTO(false)));
        List<Address> resultAddress =
                new ArrayList<>(addresses.stream()
                        .map(AddressMapper::toEntity)
                        .toList());

        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", addresses);

        assertEquals("testuser", user.getUsername(), "Username not match");
        assertEquals("testuser@gmail.com", user.getEmail(), "Email not match");
        assertEquals("test", user.getFirstName(), "FirstName not match");
        assertEquals("user", user.getLastName(), "LastName not match");
        assertEquals(2, user.getAddresses().size(), "Address size not match");
        assertEquals("+12345678981", user.getPhoneNumber(), "Phone Number not match");

        UserUpdateRequestDTO dto = new UserUpdateRequestDTO(
                "testuserupdate",
                "test123update",
                "userupdate",
                addresses,
                "+123456789811",
                "testuserupdate@gmail.com"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressService.resolveAddresses(anyList(), any(User.class))).thenReturn(resultAddress);

        User updatedUser = userService.updateUser(1L, dto);

        assertEquals("testuserupdate", updatedUser.getUsername(), "Username not match");
        assertEquals("testuserupdate@gmail.com", updatedUser.getEmail(), "Email not match");
        assertEquals("test123update", updatedUser.getFirstName(), "FirstName not match");
        assertEquals("userupdate", updatedUser.getLastName(), "LastName not match");
        assertEquals(2, updatedUser.getAddresses().size(), "Address size not match");
        assertEquals("+123456789811", updatedUser.getPhoneNumber(), "Phone Number not match");
        verify(userRepository).findById(1L);
        verify(addressService).resolveAddresses(anyList(), any(User.class));
    }

    @Test
    void patchCustomerUser() {
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddressDTO(true), createAddressDTO(false)));
        List<Address> resultAddress =
                new ArrayList<>(addresses.stream()
                        .map(AddressMapper::toEntity)
                        .toList());

        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", addresses);

        assertEquals("testuser", user.getUsername(), "Username not match");
        assertEquals("testuser@gmail.com", user.getEmail(), "Email not match");
        assertEquals("test", user.getFirstName(), "FirstName not match");
        assertEquals("user", user.getLastName(), "LastName not match");
        assertEquals(2, user.getAddresses().size(), "Address size not match");
        assertEquals("+12345678981", user.getPhoneNumber(), "Phone Number not match");

        UserUpdateRequestDTO dto = new UserUpdateRequestDTO(
                "testuserupdate",
                "test123update",
                null,
                addresses,
                null,
                null
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressService.resolveAddresses(anyList(), any(User.class))).thenReturn(resultAddress);

        User updatedUser = userService.patchUser(1L, dto);

        assertEquals("testuserupdate", updatedUser.getUsername(), "Username not match");
        assertEquals("testuser@gmail.com", updatedUser.getEmail(), "Email not match");
        assertEquals("test123update", updatedUser.getFirstName(), "FirstName not match");
        assertEquals("user", updatedUser.getLastName(), "LastName not match");
        assertEquals(2, updatedUser.getAddresses().size(), "Address size not match");
        assertEquals("+12345678981", updatedUser.getPhoneNumber(), "Phone Number not match");
        verify(userRepository).findById(1L);
        verify(addressService).resolveAddresses(anyList(), any(User.class));
    }

    @Test
    void changePassword() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());

        assertEquals("test123", user.getPassword(), "Password should be encrypted");

        String newPassword = "newpassword";

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPassword)).thenReturn("encoded");

        userService.changePassword(1L, newPassword);

        assertEquals("testuser", user.getUsername(), "Username not match");
        assertEquals("encoded", user.getPassword(), "Password should be encrypted");
        assertEquals("testuser@gmail.com", user.getEmail(), "Email not match");
        assertEquals("test", user.getFirstName(), "FirstName not match");
        assertEquals("user", user.getLastName(), "LastName not match");
        assertEquals(0, user.getAddresses().size(), "Address size not match");
        assertEquals("+12345678981", user.getPhoneNumber(), "Phone Number not match");
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
        verify(passwordEncoder).encode(newPassword);
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }
}
