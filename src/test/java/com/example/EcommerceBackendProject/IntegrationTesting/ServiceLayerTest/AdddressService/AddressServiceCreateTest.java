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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AddressServiceCreateTest {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void createAddress_success() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());

        assertEquals("123 Main st", createdAddress.getStreet());
        assertEquals("Sacramento", createdAddress.getCity());
        assertEquals("CA", createdAddress.getState());
        assertEquals("USA", createdAddress.getCountry());
        assertEquals("12345", createdAddress.getZipCode());
        assertTrue(createdAddress.getIsDefault());
        assertEquals(user.getId(), createdAddress.getUser().getId());

        Address savedAddress = addressRepository.findById(createdAddress.getId()).get();

        assertEquals("123 Main st", savedAddress.getStreet());
        assertTrue(savedAddress.getIsDefault());
        assertEquals(user.getId(), savedAddress.getUser().getId());
    }

    @Test
    void createAddress_promoteAddressToDefault() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", false);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());

        assertEquals("123 Main st", createdAddress.getStreet());
        assertEquals("Sacramento", createdAddress.getCity());
        assertEquals("CA", createdAddress.getState());
        assertEquals("USA", createdAddress.getCountry());
        assertEquals("12345", createdAddress.getZipCode());
        assertTrue(createdAddress.getIsDefault());
        assertEquals(user.getId(), createdAddress.getUser().getId());

        Address savedAddress = addressRepository.findById(createdAddress.getId()).get();

        assertEquals("123 Main st", savedAddress.getStreet());
        assertTrue(savedAddress.getIsDefault());
        assertEquals(user.getId(), savedAddress.getUser().getId());
    }

    @Test
    void createAddress_newDefaultAddress() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress("1234 Main st", "Sacramento", "CA", "USA", "12345", true);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());
        Address createdAddress1 = addressService.createAddress(addressRequestDTO1, user.getId());

        assertEquals("1234 Main st", createdAddress1.getStreet());
        assertEquals("Sacramento", createdAddress1.getCity());
        assertEquals("CA", createdAddress1.getState());
        assertEquals("USA", createdAddress1.getCountry());
        assertEquals("12345", createdAddress1.getZipCode());
        assertTrue(createdAddress1.getIsDefault());
        assertEquals(user.getId(), createdAddress.getUser().getId());

        Address savedAddress = addressRepository.findById(createdAddress.getId()).get();
        Address savedAddress1 = addressRepository.findById(createdAddress1.getId()).get();

        assertEquals("1234 Main st", savedAddress1.getStreet());
        assertTrue(savedAddress1.getIsDefault());
        assertFalse(savedAddress.getIsDefault());
        assertEquals(user.getId(), savedAddress1.getUser().getId());
    }

    @Test
    void createAddress_nonDefaultWhenDefaultExists() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress("1234 Main st", "Sacramento", "CA", "USA", "12345", false);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());
        Address createdAddress1 = addressService.createAddress(addressRequestDTO1, user.getId());

        assertEquals("1234 Main st", createdAddress1.getStreet());
        assertEquals("Sacramento", createdAddress1.getCity());
        assertEquals("CA", createdAddress1.getState());
        assertEquals("USA", createdAddress1.getCountry());
        assertEquals("12345", createdAddress1.getZipCode());
        assertFalse(createdAddress1.getIsDefault());
        assertTrue(createdAddress.getIsDefault());
        assertEquals(user.getId(), createdAddress.getUser().getId());

        Address savedAddress = addressRepository.findById(createdAddress.getId()).get();
        Address savedAddress1 = addressRepository.findById(createdAddress1.getId()).get();

        assertEquals("1234 Main st", savedAddress1.getStreet());
        assertFalse(savedAddress1.getIsDefault());
        assertTrue(savedAddress.getIsDefault());
        assertEquals(user.getId(), savedAddress1.getUser().getId());
    }

    @Test
    void createAddress_userNotFound() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.createAddress(addressRequestDTO, 999L));

        assertEquals("No user found with id: " + 999L, ex.getMessage());
    }

    @Test
    void resolveAddress_success() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress("1234 Main st", "Sacramento", "CA", "USA", "12345", false);
        AddressRequestDTO addressRequestDTO2 = AddressTestFactory.createAddress("1235 Main st", "Sacramento", "CA", "USA", "12345", false);
        AddressRequestDTO addressRequestDTO3 = AddressTestFactory.createAddress("1236 Main st", "Sacramento", "CA", "USA", "12345", false);
        List<AddressRequestDTO> addresses = List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2, addressRequestDTO3);
        User user = testDataHelper.createUser();

        List<Address> createdAddress = addressService.resolveAddresses(addresses, user);

        assertEquals(4, createdAddress.size());

        createdAddress.forEach(addr -> assertEquals(user.getId(), addr.getUser().getId()));

    }

    @Test
    void resolveAddress_emptyList() {
        List<AddressRequestDTO> addresses = List.of();
        User user = testDataHelper.createUser();

        List<Address> createdAddress = addressService.resolveAddresses(addresses, user);

        assertEquals(user.getAddresses().size(), addresses.size());
        assertEquals(user.getAddresses(), createdAddress);
    }

    @Test
    void resolveAddress_defaultAddress_onlyOne() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress("1234 Main st", "Sacramento", "CA", "USA", "12345", true);
        AddressRequestDTO addressRequestDTO2 = AddressTestFactory.createAddress("1235 Main st", "Sacramento", "CA", "USA", "12345", true);
        AddressRequestDTO addressRequestDTO3 = AddressTestFactory.createAddress("1236 Main st", "Sacramento", "CA", "USA", "12345", true);
        List<AddressRequestDTO> addresses = List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2, addressRequestDTO3);
        User user = testDataHelper.createUser();

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> addressService.resolveAddresses(addresses, user));

        assertEquals("Only one default address allowed", ex.getMessage());
    }
}
