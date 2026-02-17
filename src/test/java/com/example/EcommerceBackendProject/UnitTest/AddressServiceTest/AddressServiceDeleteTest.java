package com.example.EcommerceBackendProject.UnitTest.AddressServiceTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.AddressServiceUtils.createAddressDto;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.UserServiceTestUtils.createTestUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddressServiceDeleteTest extends BaseAddressServiceTest {

    @Test
    void deleteAddress() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);

        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", false);
        Address address = AddressMapper.toEntity(addressRequestDTO);
        address.setId(1L);
        address.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserIdAndId(1L, 1L)).thenReturn(Optional.of(address));

        addressService.deleteAddress(1L, 1L);

        verify(addressRepository).delete(address);
        verify(addressRepository, never()).findFirstByUserIdOrderByCreatedAtAsc(1L);
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void deleteAddress_noUserFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.deleteAddress(1L, 1L));

        assertEquals("No user found with id: " + 1L, ex.getMessage());
        verify(addressRepository, never()).delete(any(Address.class));
        verify(addressRepository, never()).findByUserIdAndId(1L, 1L);
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void deleteAddress_noAddressFound() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.deleteAddress(1L, 1L));

        assertEquals("Address not found", ex.getMessage());
        verify(addressRepository, never()).delete(any(Address.class));
        verify(addressRepository).findByUserIdAndId(1L, 1L);
        verify(addressRepository, never()).save(any(Address.class));
    }

    @Test
    void deleteAddress_deleteDefaultAddress() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);

        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", false);
        Address address = AddressMapper.toEntity(addressRequestDTO);
        address.setId(1L);
        address.setUser(user);

        AddressRequestDTO addressRequestDTO1 = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        Address address1 = AddressMapper.toEntity(addressRequestDTO1);
        address1.setId(2L);
        address1.setUser(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserIdAndId(1L, 2L)).thenReturn(Optional.of(address1));
        when(addressRepository.findFirstByUserIdOrderByCreatedAtAsc(1L))
                .thenReturn(Optional.of(address));

        addressService.deleteAddress(2L, 1L);

        assertTrue(address.getIsDefault());

        verify(addressRepository).delete(address1);
        verify(addressRepository).findFirstByUserIdOrderByCreatedAtAsc(1L);
        verify(addressRepository).save(address);
    }

    @Test
    void deleteAnyAddress() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);

        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", false);
        Address address = AddressMapper.toEntity(addressRequestDTO);
        address.setId(1L);
        address.setUser(user);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

        addressService.deleteAnyAddress(1L);

        verify(addressRepository).delete(address);
    }
}
