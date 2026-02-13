package com.example.EcommerceBackendProject.UnitTest.AddressServiceTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.AddressServiceUtils.*;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.UserServiceTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddressServiceCreateTest extends BaseAddressServiceTest {

    @Test
    void createAddress() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        Address address = AddressMapper.toEntity(addressRequestDTO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.save(any(Address.class))).thenAnswer(inv -> inv.getArgument(0));
        when(securityUtils.getCurrentUserId()).thenReturn(1L);
        when(securityUtils.isAdmin()).thenReturn(false);

        Address addressSaved = addressService.createAddress(addressRequestDTO, 1L);

        assertEquals("123 Main st", address.getStreet(), "Street name is not match");
        assertEquals("Sacramento", address.getCity(), "Street name is not match");
        assertEquals("CA", address.getState(), "Street name is not match");
        assertEquals("USA", address.getCountry(), "Street name is not match");
        assertEquals("12345", address.getZipCode(), "Street name is not match");
        assertTrue(address.getIsDefault(), "Address is not set as default");
    }
}
