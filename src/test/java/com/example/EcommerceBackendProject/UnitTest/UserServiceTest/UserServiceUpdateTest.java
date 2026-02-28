package com.example.EcommerceBackendProject.UnitTest.UserServiceTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.UserTestUtils.*;
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

        UserUpdateRequestDTO dto = createUserUpdateDTO(
                "testuserupdate",
                "testuserupdate@gmail.com",
                "test123update",
                "userupdate",
                "+123456789811",
                addresses
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
        verify(userRepository).existsByUsername("testuserupdate");
        verify(userRepository).existsByEmail("testuserupdate@gmail.com");
        verify(addressService).resolveAddresses(anyList(), any(User.class));
    }

    @Test
    void updateCustomerUser_userNotFound() {
        UserUpdateRequestDTO dto = createUserUpdateDTO(
                "testuserupdate",
                "testuserupdate@gmail.com",
                "test123update",
                "userupdate",
                "+123456789811",
                List.of()
        );

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> userService.updateUser(1L, dto));
        assertEquals("User not found", ex.getMessage());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).existsByUsername("testuserupdate");
        verify(userRepository, never()).existsByEmail("testuserupdate@gmail.com");
        verifyNoInteractions(addressService);
    }

    @Test
    void updateCustomerUser_duplicateUsername() {

        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());

        UserUpdateRequestDTO dto = createUserUpdateDTO(
                "testuserupdate",
                "testuserupdate@gmail.com",
                "test123update",
                "userupdate",
                "+123456789811",
                List.of()
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("testuserupdate")).thenReturn(true);
        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(1L, dto));

        assertEquals("Username is already in use", ex.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("testuserupdate");
        verifyNoInteractions(addressService);
    }

    @Test
    void updateCustomerUser_duplicateEmail() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());

        UserUpdateRequestDTO dto = createUserUpdateDTO(
                "testuserupdate",
                "testuserupdate@gmail.com",
                "test123update",
                "userupdate",
                "+123456789811",
                List.of()
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("testuserupdate@gmail.com")).thenReturn(true);
        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () -> userService.updateUser(1L, dto));

        assertEquals("Email is already in use", ex.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("testuserupdate@gmail.com");
        verifyNoInteractions(addressService);
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

        UserUpdateRequestDTO dto = createUserUpdateDTO(
                "testuserupdate",
                null,
                "test123update",
                null,
                null,
                addresses
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
        verify(userRepository).existsByUsername("testuserupdate");
        verify(userRepository, never()).existsByEmail("testuserupdate@gmail.com");
        verify(addressService).resolveAddresses(anyList(), any(User.class));
    }

    @Test
    void patchCustomerUser_validateBothUsernameEmail() {
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

        UserUpdateRequestDTO dto = createUserUpdateDTO(
                "testuserupdate",
                "testuserupdate@gmail.com",
                "test123update",
                null,
                null,
                addresses
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressService.resolveAddresses(anyList(), any(User.class))).thenReturn(resultAddress);

        User updatedUser = userService.patchUser(1L, dto);

        assertEquals("testuserupdate", updatedUser.getUsername(), "Username not match");
        assertEquals("testuserupdate@gmail.com", updatedUser.getEmail(), "Email not match");
        assertEquals("test123update", updatedUser.getFirstName(), "FirstName not match");
        assertEquals("user", updatedUser.getLastName(), "LastName not match");
        assertEquals(2, updatedUser.getAddresses().size(), "Address size not match");
        assertEquals("+12345678981", updatedUser.getPhoneNumber(), "Phone Number not match");
        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("testuserupdate");
        verify(userRepository).existsByEmail("testuserupdate@gmail.com");
        verify(addressService).resolveAddresses(anyList(), any(User.class));
    }

    @Test
    void patchCustomerUser_noValidateBothUsernameEmail() {
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

        UserUpdateRequestDTO dto = createUserUpdateDTO(
                null,
                null,
                "test123update",
                null,
                null,
                addresses
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressService.resolveAddresses(anyList(), any(User.class))).thenReturn(resultAddress);

        User updatedUser = userService.patchUser(1L, dto);

        assertEquals("testuser", updatedUser.getUsername(), "Username not match");
        assertEquals("testuser@gmail.com", updatedUser.getEmail(), "Email not match");
        assertEquals("test123update", updatedUser.getFirstName(), "FirstName not match");
        assertEquals("user", updatedUser.getLastName(), "LastName not match");
        assertEquals(2, updatedUser.getAddresses().size(), "Address size not match");
        assertEquals("+12345678981", updatedUser.getPhoneNumber(), "Phone Number not match");
        verify(userRepository).findById(1L);
        verify(userRepository, never()).existsByUsername(any());
        verify(userRepository, never()).existsByEmail(any());
        verify(addressService).resolveAddresses(anyList(), any(User.class));
    }

    @Test
    void patchCustomerUser_userNotFound() {

        UserUpdateRequestDTO dto = createUserUpdateDTO(
                "testuserupdate",
                null,
                "test123update",
                null,
                null,
                List.of()
        );

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () ->
                userService.patchUser(1L, dto));
        assertEquals("User not found", ex.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).existsByUsername("testuserupdate");
        verify(userRepository, never()).existsByEmail("testuserupdate@gmail.com");
        verifyNoInteractions(addressService);
    }

    @Test
    void patchCustomerUser_duplicateUsername() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());

        UserUpdateRequestDTO dto = createUserUpdateDTO(
                "testuserupdate",
                null,
                "test123update",
                null,
                null,
                List.of()
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("testuserupdate")).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () ->
                userService.patchUser(1L, dto));
        assertEquals("Username is already in use", ex.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("testuserupdate");
        verifyNoInteractions(addressService);
    }

    @Test
    void patchCustomerUser_duplicateEmail() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());

        UserUpdateRequestDTO dto = createUserUpdateDTO(
                "testuserupdate",
                "test@gmail.com",
                "test123update",
                null,
                null,
                List.of()
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(true);

        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class, () ->
                userService.patchUser(1L, dto));
        assertEquals("Email is already in use", ex.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("test@gmail.com");
        verifyNoInteractions(addressService);
    }

    @Test
    void changePassword() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());

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
    }

    @Test
    void changePassword_userNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> userService.changePassword(1L, "testpassword"));

        assertEquals("User not found", ex.getMessage());
        verify(userRepository).findById(1L);
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }
}
