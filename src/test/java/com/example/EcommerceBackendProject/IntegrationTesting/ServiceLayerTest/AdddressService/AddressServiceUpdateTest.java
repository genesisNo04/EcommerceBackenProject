package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.AdddressService;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.AddressUpdateRequestDTO;
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
public class AddressServiceUpdateTest {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void updateAddress() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());

        AddressRequestDTO addressUpdateRequestDTO = AddressTestFactory.createAddress("1234 Main st", "Los Angeles", "CA", "USA", "54321", true);

        Address updatedAddress = addressService.updateAddress(createdAddress.getId(), addressUpdateRequestDTO, user.getId());

        assertEquals("1234 Main st", updatedAddress.getStreet());
        assertEquals("Los Angeles", updatedAddress.getCity());
        assertEquals("CA", updatedAddress.getState());
        assertEquals("USA", updatedAddress.getCountry());
        assertEquals("54321", updatedAddress.getZipCode());
        assertTrue(updatedAddress.getIsDefault());
    }

    @Test
    void updateAddress_updateDefault() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress("1234 Main st", "Sacramento", "CA", "USA", "12345", false);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());
        Address createdAddress1 = addressService.createAddress(addressRequestDTO1, user.getId());

        AddressRequestDTO addressUpdateRequestDTO = AddressTestFactory.createAddress("1235 Main st", "Los Angeles", "CA", "USA", "54321", true);

        addressService.updateAddress(createdAddress1.getId(), addressUpdateRequestDTO, user.getId());

        Address savedAddress = addressRepository.findById(createdAddress.getId()).orElseThrow();
        Address savedAddress1 = addressRepository.findById(createdAddress1.getId()).orElseThrow();

        assertFalse(savedAddress.getIsDefault());
        assertTrue(savedAddress1.getIsDefault());
    }

    @Test
    void updateAddress_userNotFound() {
        AddressRequestDTO addressUpdateRequestDTO = AddressTestFactory.createAddress("1234 Main st", "Los Angeles", "CA", "USA", "54321", true);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.updateAddress(1L, addressUpdateRequestDTO,999L));

        assertEquals("No user found with id: " + 999L, ex.getMessage());
    }

    @Test
    void updateAddress_noAddressFound() {
        User user = testDataHelper.createUser();

        AddressRequestDTO addressUpdateRequestDTO = AddressTestFactory.createAddress("1234 Main st", "Los Angeles", "CA", "USA", "54321", true);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.updateAddress(999L, addressUpdateRequestDTO, user.getId()));

        assertEquals("No address found with id: " + 999L, ex.getMessage());
    }

    @Test
    void patchAddress() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());

        AddressUpdateRequestDTO addressUpdateRequestDTO = AddressTestFactory.createUpdateAddress("1234 Main st", "Los Angeles", "CA", "USA", "54321", true);

        Address updatedAddress = addressService.patchAddress(createdAddress.getId(), addressUpdateRequestDTO, user.getId());

        assertEquals("1234 Main st", updatedAddress.getStreet());
        assertEquals("Los Angeles", updatedAddress.getCity());
        assertEquals("CA", updatedAddress.getState());
        assertEquals("USA", updatedAddress.getCountry());
        assertEquals("54321", updatedAddress.getZipCode());
        assertTrue(updatedAddress.getIsDefault());
    }

    @Test
    void patchAddress_allNullField() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());

        AddressUpdateRequestDTO addressUpdateRequestDTO = AddressTestFactory.createUpdateAddress(null, null, null, null, null, null);

        Address updatedAddress = addressService.patchAddress(createdAddress.getId(), addressUpdateRequestDTO, user.getId());

        assertEquals("123 Main st", updatedAddress.getStreet());
        assertEquals("Sacramento", updatedAddress.getCity());
        assertEquals("CA", updatedAddress.getState());
        assertEquals("USA", updatedAddress.getCountry());
        assertEquals("12345", updatedAddress.getZipCode());
        assertTrue(updatedAddress.getIsDefault());
    }

    @Test
    void patchAddress_updateSomeField() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());

        AddressUpdateRequestDTO addressUpdateRequestDTO = AddressTestFactory.createUpdateAddress("1234 Main st", "LA", null, null, null, null);

        Address updatedAddress = addressService.patchAddress(createdAddress.getId(), addressUpdateRequestDTO, user.getId());

        assertEquals("1234 Main st", updatedAddress.getStreet());
        assertEquals("LA", updatedAddress.getCity());
        assertEquals("CA", updatedAddress.getState());
        assertEquals("USA", updatedAddress.getCountry());
        assertEquals("12345", updatedAddress.getZipCode());
        assertTrue(updatedAddress.getIsDefault());
    }

    @Test
    void patchAddress_userNotFound() {
        AddressUpdateRequestDTO addressUpdateRequestDTO = AddressTestFactory.createUpdateAddress("1234 Main st", "LA", null, null, null, null);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.patchAddress(1L, addressUpdateRequestDTO,999L));

        assertEquals("No user found with id: " + 999L, ex.getMessage());
    }

    @Test
    void patchAddress_noAddressFound() {
        User user = testDataHelper.createUser();

        AddressUpdateRequestDTO addressUpdateRequestDTO = AddressTestFactory.createUpdateAddress("1234 Main st", "LA", null, null, null, null);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.patchAddress(999L, addressUpdateRequestDTO, user.getId()));

        assertEquals("No address with this id: " + 999L, ex.getMessage());
    }

    @Test
    void patchAddress_updateDefault() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress("1234 Main st", "Sacramento", "CA", "USA", "12345", false);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());
        Address createdAddress1 = addressService.createAddress(addressRequestDTO1, user.getId());

        AddressUpdateRequestDTO addressUpdateRequestDTO = AddressTestFactory.createUpdateAddress("1235 Main st", "Los Angeles", "CA", "USA", "54321", true);

        addressService.patchAddress(createdAddress1.getId(), addressUpdateRequestDTO, user.getId());

        Address savedAddress = addressRepository.findById(createdAddress.getId()).orElseThrow();
        Address savedAddress1 = addressRepository.findById(createdAddress1.getId()).orElseThrow();

        assertFalse(savedAddress.getIsDefault());
        assertTrue(savedAddress1.getIsDefault());
    }
}
