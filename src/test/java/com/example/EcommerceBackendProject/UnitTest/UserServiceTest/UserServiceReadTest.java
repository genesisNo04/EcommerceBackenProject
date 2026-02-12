package com.example.EcommerceBackendProject.UnitTest.UserServiceTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.Entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static com.example.EcommerceBackendProject.UnitTest.UserServiceTest.UserServiceTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceReadTest extends  BaseUserServiceTest{

    @Test
    void findById() {
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddressDTO(true), createAddressDTO(false)));
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", addresses);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User fetchUser = userService.findById(1L);

        assertEquals("testuser", fetchUser.getUsername(), "Username not match");
        assertEquals("testuser@gmail.com", fetchUser.getEmail(), "Email not match");
        assertEquals("test", fetchUser.getFirstName(), "FirstName not match");
        assertEquals("user", fetchUser.getLastName(), "LastName not match");
        assertEquals(2, fetchUser.getAddresses().size(), "Address size not match");
        assertEquals("+12345678981", fetchUser.getPhoneNumber(), "Phone Number not match");
        verify(userRepository).findById(1L);
        verifyNoInteractions(passwordEncoder, addressService);
    }

    @Test
    void findByUsername() {
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddressDTO(true), createAddressDTO(false)));
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", addresses);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User fetchUser = userService.findByUsername("testuser");

        assertEquals("testuser", fetchUser.getUsername(), "Username not match");
        assertEquals("testuser@gmail.com", fetchUser.getEmail(), "Email not match");
        assertEquals("test", fetchUser.getFirstName(), "FirstName not match");
        assertEquals("user", fetchUser.getLastName(), "LastName not match");
        assertEquals(2, fetchUser.getAddresses().size(), "Address size not match");
        assertEquals("+12345678981", fetchUser.getPhoneNumber(), "Phone Number not match");
        verify(userRepository).findByUsername("testuser");
        verifyNoInteractions(passwordEncoder, addressService);
    }

    @Test
    void findByEmail() {
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddressDTO(true), createAddressDTO(false)));
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", addresses);

        when(userRepository.findByEmail("testuser@gmail.com")).thenReturn(Optional.of(user));

        User fetchUser = userService.findByEmail("testuser@gmail.com");

        assertEquals("testuser", fetchUser.getUsername(), "Username not match");
        assertEquals("testuser@gmail.com", fetchUser.getEmail(), "Email not match");
        assertEquals("test", fetchUser.getFirstName(), "FirstName not match");
        assertEquals("user", fetchUser.getLastName(), "LastName not match");
        assertEquals(2, fetchUser.getAddresses().size(), "Address size not match");
        assertEquals("+12345678981", fetchUser.getPhoneNumber(), "Phone Number not match");
        verify(userRepository).findByEmail("testuser@gmail.com");
        verifyNoInteractions(passwordEncoder, addressService);
    }

    @Test
    void findById_returnSingleItemPage() {
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddressDTO(true), createAddressDTO(false)));
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", addresses);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Page<User> fetchUser = userService.findById(1L, pageable);

        assertEquals(1, fetchUser.getTotalElements(), "Total element should only be 1");
        assertEquals(1, fetchUser.getContent().size(), "Total element should only be 1");
        assertEquals("testuser", fetchUser.getContent().get(0).getUsername(), "Username does not match");
        verify(userRepository).findById(1L);
        verifyNoInteractions(passwordEncoder, addressService);
    }

    @Test
    void findByUsername_returnSingleItemPage() {
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddressDTO(true), createAddressDTO(false)));
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", addresses);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Page<User> fetchUser = userService.findByUsername("testuser", pageable);

        assertEquals(1, fetchUser.getTotalElements(), "Total element should only be 1");
        assertEquals(1, fetchUser.getContent().size(), "Total element should only be 1");
        assertEquals("testuser", fetchUser.getContent().get(0).getUsername(), "Username does not match");
        verify(userRepository).findByUsername("testuser");
        verifyNoInteractions(passwordEncoder, addressService);
    }

    @Test
    void findByEmail_returnSingleItemPage() {
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddressDTO(true), createAddressDTO(false)));
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", addresses);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByEmail("testuser@gmail.com")).thenReturn(Optional.of(user));

        Page<User> fetchUser = userService.findByEmail("testuser@gmail.com", pageable);

        assertEquals(1, fetchUser.getTotalElements(), "Total element should only be 1");
        assertEquals(1, fetchUser.getContent().size(), "Total element should only be 1");
        assertEquals("testuser", fetchUser.getContent().get(0).getUsername(), "Username does not match");
        verify(userRepository).findByEmail("testuser@gmail.com");
        verifyNoInteractions(passwordEncoder, addressService);
    }

    @Test
    void findAllUser() {
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", "test", "user", "+12345678981", List.of());
        User user1 = createTestUser("testuser1", "test123", "testuser1@gmail.com", "test1", "user1", "+12345678981", List.of());
        User user2 = createTestUser("testuser2", "test123", "testuser2@gmail.com", "test2", "user2", "+12345678981", List.of());
        User user3 = createTestUser("testuser3", "test123", "testuser3@gmail.com", "test3", "user3", "+12345678981", List.of());
        User user4 = createTestUser("testuser4", "test123", "testuser4@gmail.com", "test4", "user4", "+12345678981", List.of());

        List<User> users = List.of(user, user1, user2, user3, user4);

        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> fetchUser = userService.findAll(pageable);

        assertEquals(5, fetchUser.getTotalElements(), "Total element should only be 5");
        assertEquals(5, fetchUser.getContent().size(), "Total element should only be 5");
        assertEquals(1, fetchUser.getTotalPages(), "Total page should only be 1");
        verify(userRepository).findAll(pageable);
        verifyNoInteractions(passwordEncoder, addressService);
    }
}
