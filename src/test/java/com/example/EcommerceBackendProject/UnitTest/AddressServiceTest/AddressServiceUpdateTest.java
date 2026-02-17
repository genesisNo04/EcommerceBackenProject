package com.example.EcommerceBackendProject.UnitTest.AddressServiceTest;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.AddressUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.Role;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.AddressServiceUtils.*;
import static com.example.EcommerceBackendProject.UnitTest.Utilities.UserServiceTestUtils.createTestUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddressServiceUpdateTest extends BaseAddressServiceTest {

    @Test
    void updateAddress() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);
        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        Address address = AddressMapper.toEntity(addressRequestDTO);
        address.setId(1L);
        address.setUser(user);

        AddressRequestDTO updateAddressDTO = createAddressDto("1234 Main st", "Tucson", "OK", "USA", "54321", true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserIdAndId(1L, 1L)).thenReturn(Optional.of(address));

        Address addressSaved = addressService.updateAddress(1L, updateAddressDTO, 1L);

        assertEquals("1234 Main st", addressSaved.getStreet(), "Street name is not match");
        assertEquals("Tucson", addressSaved.getCity(), "Street name is not match");
        assertEquals("OK", addressSaved.getState(), "Street name is not match");
        assertEquals("USA", addressSaved.getCountry(), "Street name is not match");
        assertEquals("54321", addressSaved.getZipCode(), "Street name is not match");
        assertEquals(1L, addressSaved.getUser().getId(), "User does not match");
        assertTrue(addressSaved.getIsDefault(), "Address is not set as default");
        verify(addressRepository).resetDefaultForUser(1L);
    }

    @Test
    void updateAddress_noUserFound() {
        AddressRequestDTO updateAddressDTO = createAddressDto("1234 Main st", "Los Angeles", "CA", "USA", "54321", true);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.updateAddress(1L, updateAddressDTO, 1L));

        assertEquals("No user found with id: " + 1L, ex.getMessage(), "Error message does not match");
        verify(addressRepository, never()).findByUserIdAndId(1L, 1L);
        verify(addressRepository, never()).resetDefaultForUser(1L);
    }

    @Test
    void updateAddress_noAddressFound() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);

        AddressRequestDTO updateAddressDTO = createAddressDto("1234 Main st", "Los Angeles", "CA", "USA", "54321", true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.updateAddress(1L, updateAddressDTO, 1L));

        assertEquals("No address with this id: " + 1L, ex.getMessage(), "Error message does not match");
        verify(addressRepository).findByUserIdAndId(1L, 1L);
        verify(addressRepository, never()).resetDefaultForUser(1L);
    }

    @Test
    void patchAddress() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);

        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        Address address = AddressMapper.toEntity(addressRequestDTO);
        address.setId(1L);
        address.setUser(user);

        AddressUpdateRequestDTO updateAddressDTO = createUpdateAddressDto("1234 Main st", "Los Angeles", null, null, "54321", true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserIdAndId(1L, 1L)).thenReturn(Optional.of(address));

        Address addressSaved = addressService.patchAddress(1L, updateAddressDTO, 1L);

        assertEquals("1234 Main st", addressSaved.getStreet(), "Street name is not match");
        assertEquals("Los Angeles", addressSaved.getCity(), "Street name is not match");
        assertEquals("CA", addressSaved.getState(), "Street name is not match");
        assertEquals("USA", addressSaved.getCountry(), "Street name is not match");
        assertEquals("54321", addressSaved.getZipCode(), "Street name is not match");
        assertEquals(1L, addressSaved.getUser().getId(), "User does not match");
        assertTrue(addressSaved.getIsDefault(), "Address is not set as default");
        verify(addressRepository, never()).resetDefaultForUser(1L);
    }

    @Test
    void patchAddress_defaultFalse() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);

        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", false);
        Address address = AddressMapper.toEntity(addressRequestDTO);
        address.setId(1L);
        address.setUser(user);

        AddressRequestDTO addressRequestDTO1 = createAddressDto("1235 Main st", "Sacramento", "CA", "USA", "12345", true);
        Address address1 = AddressMapper.toEntity(addressRequestDTO1);
        address1.setId(2L);
        address1.setUser(user);

        AddressUpdateRequestDTO updateAddressDTO = createUpdateAddressDto("1234 Main st", "Los Angeles", null, null, "54321", false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserIdAndId(1L, 2L)).thenReturn(Optional.of(address1));
        when(addressRepository.findFirstByUserIdOrderByCreatedAtAsc(1L))
                .thenReturn(Optional.of(address));

        Address addressSaved = addressService.patchAddress(2L, updateAddressDTO, 1L);

        assertFalse(addressSaved.getIsDefault());
        assertTrue(address.getIsDefault());
        verify(addressRepository, never()).resetDefaultForUser(1L);
        verify(addressRepository).findFirstByUserIdOrderByCreatedAtAsc(1L);
        verify(addressRepository).save(address);
    }

    @Test
    void patchAddress_defaultTrue() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);

        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        Address address = AddressMapper.toEntity(addressRequestDTO);
        address.setId(1L);
        address.setUser(user);

        AddressRequestDTO addressRequestDTO1 = createAddressDto("1235 Main st", "Sacramento", "CA", "USA", "12345", false);
        Address address1 = AddressMapper.toEntity(addressRequestDTO1);
        address1.setId(2L);
        address1.setUser(user);

        AddressUpdateRequestDTO updateAddressDTO = createUpdateAddressDto("1234 Main st", "Los Angeles", null, null, "54321", true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserIdAndId(1L, 2L)).thenReturn(Optional.of(address1));

        Address addressSaved = addressService.patchAddress(2L, updateAddressDTO, 1L);

        assertTrue(addressSaved.getIsDefault());
        verify(addressRepository).resetDefaultForUser(1L);
        verify(addressRepository, never()).findFirstByUserIdOrderByCreatedAtAsc(1L);
        verify(addressRepository, never()).save(address);
    }

    @Test
    void setDefaultAddress() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);

        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", false);
        Address address = AddressMapper.toEntity(addressRequestDTO);
        address.setId(1L);
        address.setUser(user);

        when(addressRepository.findByUserIdAndId(1L, 1L)).thenReturn(Optional.of(address));

        addressService.setDefaultAddress(1L, 1L);

        InOrder inorder = inOrder(addressRepository);

        inorder.verify(addressRepository).resetDefaultForUser(1L);
        inorder.verify(addressRepository).updateDefaultForUser(1L, 1L);
    }

    @Test
    void setDefaultAddress_noAddressFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.setDefaultAddress(1L, 1L));

        assertEquals("Address not found", ex.getMessage());

        verify(addressRepository, never()).resetDefaultForUser(1L);
        verify(addressRepository, never()).updateDefaultForUser(1L, 1L);
    }

    @Test
    void patchAddress_updateAnyAddress() {
        User user = createTestUser("testuser", "test123", "test@gmail.com", "test", "user", "+12345678951", List.of());
        user.setId(1L);

        AddressRequestDTO addressRequestDTO = createAddressDto("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        Address address = AddressMapper.toEntity(addressRequestDTO);
        address.setId(1L);
        address.setUser(user);

        AddressUpdateRequestDTO updateAddressDTO = createUpdateAddressDto("1234 Main st", "Los Angeles", null, null, "54321", true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(addressRepository.findByUserIdAndId(1L, 1L)).thenReturn(Optional.of(address));

        Address addressSaved = addressService.patchAddress(1L, updateAddressDTO, 1L);

        assertEquals("1234 Main st", addressSaved.getStreet(), "Street name is not match");
        assertEquals("Los Angeles", addressSaved.getCity(), "Street name is not match");
        assertEquals("CA", addressSaved.getState(), "Street name is not match");
        assertEquals("USA", addressSaved.getCountry(), "Street name is not match");
        assertEquals("54321", addressSaved.getZipCode(), "Street name is not match");
        assertEquals(1L, addressSaved.getUser().getId(), "User does not match");
        assertTrue(addressSaved.getIsDefault(), "Address is not set as default");
        verify(addressRepository, never()).resetDefaultForUser(1L);
    }

}
