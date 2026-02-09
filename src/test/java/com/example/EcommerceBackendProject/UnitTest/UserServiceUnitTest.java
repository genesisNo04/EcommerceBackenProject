package com.example.EcommerceBackendProject.UnitTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.UserRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import com.example.EcommerceBackendProject.Mapper.UserMapper;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.AddressService;
import com.example.EcommerceBackendProject.Service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private AddressService addressService;

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

    private User createTestUser(String username, String password, String email, List<AddressRequestDTO> addresses) {
        UserRequestDTO userRequestDTO = new UserRequestDTO(
                username,
                password,
                email,
                "test",
                "user",
                addresses,
                "+12345678981");
        List<Address> outputAddress = addresses.stream().map(AddressMapper::toEntity).toList();
        User user = UserMapper.toEntity(userRequestDTO);
        user.setPassword("test123");
        user.setAddresses(outputAddress);
        return user;
    }

    @Test
    void createUser() {
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddress(true), createAddress(false)));
        List<Address> resultAddress = addresses.stream().map(AddressMapper::toEntity).toList();

        UserRequestDTO dto = new UserRequestDTO(
                "testuser",
                "test123",
                "test@gmail.com",
                "test",
                "user",
                addresses,
                "+123456789"
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
        verify(passwordEncoder).encode("test123");
        verify(userRepository).save(any(User.class));
        verify(addressService).resolveAddresses(anyList(), any(User.class));
    }

    @Test
    void findById() {
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddress(true), createAddress(false)));
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", addresses);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        User fetchUser = userService.findById(1L);

        assertEquals("testuser", fetchUser.getUsername(), "Username not match");
        assertEquals("test123", fetchUser.getPassword(), "Password does not match");
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
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddress(true), createAddress(false)));
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", addresses);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User fetchUser = userService.findByUsername("testuser");

        assertEquals("testuser", fetchUser.getUsername(), "Username not match");
        assertEquals("test123", fetchUser.getPassword(), "Password does not match");
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
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddress(true), createAddress(false)));
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", addresses);

        when(userRepository.findByEmail("testuser@gmail.com")).thenReturn(Optional.of(user));

        User fetchUser = userService.findByEmail("testuser@gmail.com");

        assertEquals("testuser", fetchUser.getUsername(), "Username not match");
        assertEquals("test123", fetchUser.getPassword(), "Password does not match");
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
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddress(true), createAddress(false)));
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", addresses);
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
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddress(true), createAddress(false)));
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", addresses);
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
        List<AddressRequestDTO> addresses = new ArrayList<>(List.of(createAddress(true), createAddress(false)));
        User user = createTestUser("testuser", "test123", "testuser@gmail.com", addresses);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByEmail("testuser@gmail.com")).thenReturn(Optional.of(user));

        Page<User> fetchUser = userService.findByEmail("testuser@gmail.com", pageable);

        assertEquals(1, fetchUser.getTotalElements(), "Total element should only be 1");
        assertEquals(1, fetchUser.getContent().size(), "Total element should only be 1");
        assertEquals("testuser", fetchUser.getContent().get(0).getUsername(), "Username does not match");
        verify(userRepository).findByEmail("testuser@gmail.com");
        verifyNoInteractions(passwordEncoder, addressService);
    }
}
