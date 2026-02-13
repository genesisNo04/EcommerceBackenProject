package com.example.EcommerceBackendProject.UnitTest.UserServiceTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.Role;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.UserServiceTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserServiceCreateTest extends BaseUserServiceTest{

    private void assertDuplicateThrows(UserRequestDTO dto) {
        ResourceAlreadyExistsException ex = assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.createCustomerUser(dto));

        assertEquals("username or email is already exists.", ex.getMessage());
    }

    @Test
    void createCustomerUser() {
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddressDTO(true), createAddressDTO(false)));
        List<Address> resultAddress = addresses.stream().map(AddressMapper::toEntity).toList();

        UserRequestDTO dto = createUserDTO(
                "testuser",
                "test123",
                "test@gmail.com",
                "test",
                "user",
                "+123456789",
                addresses

        );

        when(passwordEncoder.encode(anyString())).thenReturn("saved-password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        when(addressService.resolveAddresses(anyList(), any(User.class))).thenReturn(resultAddress);

        User createdUser = userService.createCustomerUser(dto);

        assertEquals("testuser", createdUser.getUsername(), "Username not match");
        assertEquals("saved-password", createdUser.getPassword(), "Password should be encrypted");
        assertEquals("test@gmail.com", createdUser.getEmail(), "Email not match");
        assertEquals("test", createdUser.getFirstName(), "FirstName not match");
        assertEquals("user", createdUser.getLastName(), "LastName not match");
        assertEquals(2, createdUser.getAddresses().size(), "Address size not match");
        assertEquals("+123456789", createdUser.getPhoneNumber(), "Phone Number not match");
        assertTrue(createdUser.getRoles().stream().anyMatch(r -> r.equals(Role.USER)));
        assertFalse(createdUser.getRoles().stream().anyMatch(r -> r.equals(Role.ADMIN)));

        verify(passwordEncoder).encode("test123");
        verify(userRepository).save(any(User.class));
        verify(addressService).resolveAddresses(anyList(), any(User.class));
    }

    @Test
    void createCustomerUser_DuplicateUsername() {

        UserRequestDTO dto = createUserDTO(
                "testuser",
                "test123",
                "test@gmail.com",
                "test",
                "user",
                "+123456789",
                List.of()
        );

        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertDuplicateThrows(dto);

        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder, addressService);
    }

    @Test
    void createCustomerUser_DuplicateEmail() {

        UserRequestDTO dto = createUserDTO(
                "testuser",
                "test123",
                "test@gmail.com",
                "test",
                "user",
                "+123456789",
                List.of()

        );

        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(true);

        assertDuplicateThrows(dto);

        verify(userRepository).existsByEmail("test@gmail.com");
        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder, addressService);
    }

    @Test
    void createCustomerUser_DuplicateUserNameAndEmail() {

        UserRequestDTO dto = createUserDTO(
                "testuser",
                "test123",
                "test@gmail.com",
                "test",
                "user",
                "+123456789",
                List.of()

        );

        when(userRepository.existsByEmail("test@gmail.com")).thenReturn(true);

        assertDuplicateThrows(dto);

        verify(userRepository, never()).save(any());
        verifyNoInteractions(passwordEncoder, addressService);
    }

    @Test
    void createAdmin() {
        List<AddressRequestDTO> addresses = List.of(createAddressDTO(true), createAddressDTO(false));
        UserRequestDTO userRequestDTO = createUserDTO(
                "AdminUser",
                "admin",
                "admin@gmail.com",
                "test",
                "user",
                "+12345678981",
                addresses);

        when(passwordEncoder.encode("admin")).thenReturn("adminpass");

        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        when(addressService.resolveAddresses(anyList(), any(User.class))).thenReturn(addresses.stream().map(AddressMapper::toEntity).toList());

        User createdUser = userService.createAdmin(userRequestDTO);

        assertEquals("AdminUser", createdUser.getUsername(), "Username not match");
        assertEquals("adminpass", createdUser.getPassword(), "Password is not encrypted");
        assertEquals("admin@gmail.com", createdUser.getEmail(), "Email not match");
        assertEquals("test", createdUser.getFirstName(), "FirstName not match");
        assertEquals("user", createdUser.getLastName(), "LastName not match");
        assertEquals(2, createdUser.getAddresses().size(), "Address size not match");
        assertEquals("+12345678981", createdUser.getPhoneNumber(), "Phone Number not match");
        assertTrue(createdUser.getRoles().stream().anyMatch(r -> r.equals(Role.ADMIN)));
        assertFalse(createdUser.getRoles().stream().anyMatch(r -> r.equals(Role.USER)));

        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("admin");
    }
}
