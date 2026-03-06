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

        Address updatedAddress = addressService.updateAddress(createdAddress1.getId(), addressUpdateRequestDTO, user.getId());

        assertEquals("1235 Main st", updatedAddress.getStreet());
        assertEquals("Los Angeles", updatedAddress.getCity());
        assertEquals("CA", updatedAddress.getState());
        assertEquals("USA", updatedAddress.getCountry());
        assertEquals("54321", updatedAddress.getZipCode());
        assertTrue(updatedAddress.getIsDefault());

        Address savedAddress1 = addressRepository.findById(createdAddress1.getId()).orElseThrow();
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
}
