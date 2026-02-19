package com.example.EcommerceBackendProject.UnitTest.AddressServiceTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.AddressTestUtils.*;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.UserServiceTestUtils.createTestUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddressServiceReadTest extends BaseAddressServiceTest {

    @Test
    void findDefaultAddress() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        Address address = AddressMapper.toEntity(addressRequestDTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserIdAndIsDefaultTrue(1L)).thenReturn(Optional.of(address));

        Address addressSaved = addressService.getDefaultAddress(1L);

        assertEquals("123 Main st", addressSaved.getStreet(), "Street name is not match");
        assertEquals("Sacramento", addressSaved.getCity(), "Street name is not match");
        assertEquals("CA", addressSaved.getState(), "Street name is not match");
        assertEquals("USA", addressSaved.getCountry(), "Street name is not match");
        assertEquals("12345", addressSaved.getZipCode(), "Street name is not match");
        assertTrue(addressSaved.getIsDefault(), "Address is not set as default");
        verify(addressRepository, never()).resetDefaultForUser(1L);
    }

    @Test
    void findDefaultAddress_userNotFound() {
        Long userId = 1L;
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.getDefaultAddress(userId));

        assertEquals("No user found with id: " + userId, ex.getMessage());
        verify(addressRepository, never()).findByUserIdAndIsDefaultTrue(1L);
    }

    @Test
    void findDefaultAddress_noDefaultFound() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.getDefaultAddress(1L));

        assertEquals("User does not have a default address", ex.getMessage());
    }

    @Test
    void getUserAddresses() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);
        Address address = createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        Address address1 = createAddress("124 Main st", "Sacramento", "CA", "USA", "12345", false);
        Address address2 = createAddress("125 Main st", "Sacramento", "CA", "USA", "12345", false);
        Address address3 = createAddress("126 Main st", "Sacramento", "CA", "USA", "12345", false);
        Address address4 = createAddress("127 Main st", "Sacramento", "CA", "USA", "12345", false);

        List<Address> addresses = List.of(address, address1, address2, address3, address4);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> returnedPage = new PageImpl<>(addresses, pageable, addresses.size());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserId(1L, pageable)).thenReturn(returnedPage);

        Page<Address> result = addressService.getUserAddresses(1L, pageable);

        assertEquals(5, result.getTotalElements(), "Total element should only be 5");
        assertEquals(5, result.getContent().size(), "Total element should only be 5");
        assertEquals(1, result.getTotalPages(), "Total page should only be 1");
        verify(addressRepository).findByUserId(1L, pageable);
    }

    @Test
    void getUserAddresses_noUserFound() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = 1L;
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.getUserAddresses(userId, pageable));

        assertEquals("No user found with id: " + userId, ex.getMessage(), "Total element should only be 5");
        verify(addressRepository, never()).findByUserId(userId, pageable);
    }

    @Test
    void findAllAddress() {
        Address address = createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        Address address1 = createAddress("124 Main st", "Sacramento", "CA", "USA", "12345", false);
        Address address2 = createAddress("125 Main st", "Sacramento", "CA", "USA", "12345", false);
        Address address3 = createAddress("126 Main st", "Sacramento", "CA", "USA", "12345", false);
        Address address4 = createAddress("127 Main st", "Sacramento", "CA", "USA", "12345", false);
        List<Address> addresses = new ArrayList<>(List.of(address, address1, address2, address3, address4));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Address> page = new PageImpl<>(addresses, pageable, addresses.size());

        when(addressRepository.findAll(pageable)).thenReturn(page);

        Page<Address> fetchAddresses = addressService.findAllAddress(pageable);

        assertEquals(5, fetchAddresses.getTotalElements(), "Total element should only be 5");
        assertEquals(5, fetchAddresses.getContent().size(), "Total element should only be 5");
        assertEquals(1, fetchAddresses.getTotalPages(), "Total page should only be 1");
        verify(addressRepository).findAll(pageable);
    }
}
