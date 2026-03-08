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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AddressServiceReadTest {

    @Autowired
    private AddressService addressService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void findUserAddress() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress("1234 Main st", "Sacramento", "CA", "USA", "12345", false);
        AddressRequestDTO addressRequestDTO2 = AddressTestFactory.createAddress("1235 Main st", "Sacramento", "CA", "USA", "12345", false);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());
        Address createdAddress1 = addressService.createAddress(addressRequestDTO1, user.getId());
        Address createdAddress2 = addressService.createAddress(addressRequestDTO2, user.getId());
        List<Address> addresses = List.of(createdAddress, createdAddress1, createdAddress2);

        Pageable pageable = PageRequest.of(0, 10);

        Page<Address> searchResult = addressService.getUserAddresses(user.getId(), pageable);

        assertEquals(3, searchResult.getTotalElements());
        assertEquals(3, searchResult.getContent().size());
        assertEquals(addresses.stream().map(Address::getId).collect(Collectors.toSet()), searchResult.stream().map(Address::getId).collect(Collectors.toSet()));
    }

    @Test
    void findUserAddress_noUserFound() {
        Pageable pageable = PageRequest.of(0, 10);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.getUserAddresses(999L, pageable));

        assertEquals("No user found with id: " + 999L, ex.getMessage());
    }

    @Test
    void findUserDefaultAddress() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress("1234 Main st", "Sacramento", "CA", "USA", "12345", false);
        AddressRequestDTO addressRequestDTO2 = AddressTestFactory.createAddress("1235 Main st", "Sacramento", "CA", "USA", "12345", false);

        User user = testDataHelper.createUser();

        Address createdAddress = addressService.createAddress(addressRequestDTO, user.getId());
        Address createdAddress1 = addressService.createAddress(addressRequestDTO1, user.getId());
        Address createdAddress2 = addressService.createAddress(addressRequestDTO2, user.getId());
        List<Address> addresses = List.of(createdAddress, createdAddress1, createdAddress2);

        Address searchResult = addressService.getDefaultAddress(user.getId());

        assertEquals(createdAddress.getId(), searchResult.getId());
    }

    @Test
    void findUserDefaultAddress_userNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> addressService.getDefaultAddress(999l));

        assertEquals("No user found with id: " + 999L, ex.getMessage());
    }

    @Test
    void findAll() {
        AddressRequestDTO addressRequestDTO = AddressTestFactory.createAddress("123 Main st", "Sacramento", "CA", "USA", "12345", true);
        AddressRequestDTO addressRequestDTO1 = AddressTestFactory.createAddress("1234 Main st", "Sacramento", "CA", "USA", "12345", false);
        AddressRequestDTO addressRequestDTO2 = AddressTestFactory.createAddress("1235 Main st", "Sacramento", "CA", "USA", "12345", false);
        AddressRequestDTO addressRequestDTO3 = AddressTestFactory.createAddress("1236 Main st", "Sacramento", "CA", "USA", "12345", false);
        List<AddressRequestDTO> addresses = List.of(addressRequestDTO, addressRequestDTO1, addressRequestDTO2, addressRequestDTO3);
        User user = testDataHelper.createUser("testuser", "test123", "test@gmail.com", "test", "user", "+1234567890", addresses);
        Pageable pageable = PageRequest.of(0, 10);

        Page<Address> returnAddresses = addressService.findAllAddress(pageable);

        assertEquals(4, returnAddresses.getContent().size());
        assertEquals(4, returnAddresses.getTotalElements());
        assertEquals(user.getAddresses().size(), returnAddresses.getContent().size());
        assertEquals(new ArrayList<>(user.getAddresses()), new ArrayList<>(returnAddresses.getContent()));
    }
}
