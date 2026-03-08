package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.AdddressService;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.AddressTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Repository.AddressRepository;
import com.example.EcommerceBackendProject.Service.AddressService;
import com.example.EcommerceBackendProject.Service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AddressServiceDeleteAddress {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void deleteAddress() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());

        addressService.deleteAddress(createdAddress.getId(), user.getId());

        Address searchAddress = addressRepository.findById(createdAddress.getId()).orElse(null);

        assertNull(searchAddress);
    }

    @Test
    void deleteAddress_userNotFound() {
        AddressRequestDTO addressUpdateRequestDTO = AddressTestFactory.createAddress("1234 Main st", "Los Angeles", "CA", "USA", "54321", true);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.deleteAddress(1L, 999L));

        assertEquals("No user found with id: " + 999L, ex.getMessage());
    }

    @Test
    void deleteAddress_noAddressFound() {
        User user = testDataHelper.createUser();

        AddressRequestDTO addressUpdateRequestDTO = AddressTestFactory.createAddress("1234 Main st", "Los Angeles", "CA", "USA", "54321", true);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.deleteAddress(999L, user.getId()));

        assertEquals("Address not found", ex.getMessage());
    }

    @Test
    void deleteDefaultAddress() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress("1234 Main st", "Sacramento", "CA", "USA", "12345", false);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());
        Address createdAddress1 = addressService.createAddress(addressRequestDTO1, user.getId());

        addressService.deleteAddress(createdAddress.getId(), user.getId());

        Address searchAddress = addressRepository.findById(createdAddress.getId()).orElse(null);
        Address searchAddress1 = addressRepository.findById(createdAddress1.getId()).orElse(null);

        assertTrue(searchAddress1.getIsDefault());
        assertNull(searchAddress);
    }

    @Test
    void deleteAnyAddress() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());

        addressService.deleteAnyAddress(createdAddress.getId());

        Address searchAddress = addressRepository.findById(createdAddress.getId()).orElse(null);

        assertNull(searchAddress);
    }

    @Test
    void deleteAnyAddress_noAddressFound() {
        User user = testDataHelper.createUser();

        AddressRequestDTO addressUpdateRequestDTO = AddressTestFactory.createAddress("1234 Main st", "Los Angeles", "CA", "USA", "54321", true);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.deleteAnyAddress(999L));

        assertEquals("Address not found", ex.getMessage());
    }

    @Test
    void deleteAnyAddress_deleteDefault() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress("1234 Main st", "Sacramento", "CA", "USA", "12345", false);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());
        Address createdAddress1 = addressService.createAddress(addressRequestDTO1, user.getId());

        addressService.deleteAnyAddress(createdAddress.getId());

        Address searchAddress = addressRepository.findById(createdAddress.getId()).orElse(null);
        Address searchAddress1 = addressRepository.findById(createdAddress1.getId()).orElse(null);

        assertTrue(searchAddress1.getIsDefault());
        assertNull(searchAddress);
    }
}
