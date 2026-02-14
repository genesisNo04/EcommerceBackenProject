package com.example.EcommerceBackendProject.UnitTest.AddressServiceTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.AddressServiceUtils.createAddressDto;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.UserServiceTestUtils.createTestUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddressServiceUpdateTest extends BaseAddressServiceTest {

    @Test
    void updateAddress() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);
        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        Address address = AddressMapper.toEntity(addressRequestDTO);

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
}
