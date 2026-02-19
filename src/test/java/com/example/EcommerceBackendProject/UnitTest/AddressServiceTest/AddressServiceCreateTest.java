package com.example.EcommerceBackendProject.UnitTest.AddressServiceTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.AddressTestUtils.*;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.UserServiceTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddressServiceCreateTest extends BaseAddressServiceTest {

    @Test
    void createAddress_setNewDefault() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);
        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.save(any(Address.class))).thenAnswer(inv -> inv.getArgument(0));

        Address addressSaved = addressService.createAddress(addressRequestDTO, 1L);

        assertEquals("123 Main st", addressSaved.getStreet(), "Street name is not match");
        assertEquals("Sacramento", addressSaved.getCity(), "Street name is not match");
        assertEquals("CA", addressSaved.getState(), "Street name is not match");
        assertEquals("USA", addressSaved.getCountry(), "Street name is not match");
        assertEquals("12345", addressSaved.getZipCode(), "Street name is not match");
        assertEquals(1L, addressSaved.getUser().getId(), "User does not match");
        assertTrue(addressSaved.getIsDefault(), "Address is not set as default");
        verify(addressRepository).resetDefaultForUser(1L);
    }

    @Test
    void createAddress_fallbackSetDefault() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);
        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.save(any(Address.class))).thenAnswer(inv -> inv.getArgument(0));

        Address addressSaved = addressService.createAddress(addressRequestDTO, 1L);

        assertTrue(addressSaved.getIsDefault(), "Address is not set as default");
        verify(addressRepository, never()).resetDefaultForUser(1L);
        verify(addressRepository).existsByUserIdAndIsDefaultTrue(1L);
    }

    @Test
    void createAddress_fallbackNotSetDefault() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);
        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", false);
        Address address = AddressMapper.toEntity(addressRequestDTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.existsByUserIdAndIsDefaultTrue(1L)).thenReturn(true);
        when(addressRepository.save(any(Address.class))).thenAnswer(inv -> inv.getArgument(0));

        Address addressSaved = addressService.createAddress(addressRequestDTO, 1L);

        assertFalse(addressSaved.getIsDefault(), "Address is set default when it should not");
        verify(addressRepository, never()).resetDefaultForUser(1L);
        verify(addressRepository).existsByUserIdAndIsDefaultTrue(1L);
    }

    @Test
    void resolveAddress_nullList() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        List<Address> addresses = addressService.resolveAddresses(null, user);

        assertEquals(0, addresses.size());
    }

    @Test
    void resolveAddress_emptyList() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        List<Address> addresses = addressService.resolveAddresses(List.of(), user);

        assertEquals(0, addresses.size());
    }

    @Test
    void resolveAddress_oneDefault() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);
        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", false);
        AddressRequestDTO addressRequestDTO1 = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", true);

        List<Address> addresses = addressService.resolveAddresses(List.of(addressRequestDTO, addressRequestDTO1), user);

        assertEquals(2, addresses.size());
    }

    @Test
    void resolveAddress_twoDefault() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);
        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        AddressRequestDTO addressRequestDTO1 = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> addressService.resolveAddresses(List.of(addressRequestDTO, addressRequestDTO1), user));

        assertEquals("Only one default address allowed", ex.getMessage());
    }
}
