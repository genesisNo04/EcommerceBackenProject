package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.AddressUpdateRequestDTO;
import com.example.EcommerceBackendProject.DTO.CategoryRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.Category;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.NoUserFoundException;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import com.example.EcommerceBackendProject.Mapper.CategoryMapper;
import com.example.EcommerceBackendProject.Repository.AddressRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.AddressService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Address> getUserAddresses(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found with id: " + userId));
        return addressRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Address createAddress(AddressRequestDTO addressRequestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found with id: " + userId));
        Address address = AddressMapper.toEntity(addressRequestDTO);
        address.setUser(user);

        if (address.isDefault()) {
            addressRepository.resetDefaultForUser(userId);
        } else if (!addressRepository.existsByUserIdAndIsDefaultTrue(userId)) {
            address.setDefault(true);
        }

        return addressRepository.save(address);
    }


    @Override
    public Address getDefaultAddress(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found with id: " + userId));

        return addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new NoResourceFoundException("User does not have a default address"));
    }

    @Override
    @Transactional
    public Address updateAddress(Long addressId, AddressUpdateRequestDTO addressUpdateRequestDTO, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found with id: " + userId));

        Address updatedAddress = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new NoResourceFoundException("No address with this id: "+ addressId));

        updatedAddress.setStreet(addressUpdateRequestDTO.getStreet());
        updatedAddress.setState(addressUpdateRequestDTO.getState());
        updatedAddress.setCity(addressUpdateRequestDTO.getCity());
        updatedAddress.setCountry(addressUpdateRequestDTO.getCountry());
        updatedAddress.setZipCode(addressUpdateRequestDTO.getZipCode());

        if (addressUpdateRequestDTO.isDefault()) {
            addressRepository.resetDefaultForUser(userId);
        }

        updatedAddress.setDefault(addressUpdateRequestDTO.isDefault());
        return updatedAddress;
    }

    @Override
    @Transactional
    public Address patchAddress(Long addressId, AddressUpdateRequestDTO addressUpdateRequestDTO, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found with id: " + userId));

        Address updatedAddress = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new NoResourceFoundException("No address with this id: "+ addressId));

        if (addressUpdateRequestDTO.getStreet() != null) {
            updatedAddress.setStreet(addressUpdateRequestDTO.getStreet());
        }

        if (addressUpdateRequestDTO.getState() != null) {
            updatedAddress.setState(addressUpdateRequestDTO.getState());
        }

        if (addressUpdateRequestDTO.getCity() != null) {
            updatedAddress.setCity(addressUpdateRequestDTO.getCity());
        }

        if (addressUpdateRequestDTO.getCountry() != null) {
            updatedAddress.setCountry(addressUpdateRequestDTO.getCountry());
        }

        if (addressUpdateRequestDTO.getZipCode() != null) {
            updatedAddress.setZipCode(addressUpdateRequestDTO.getZipCode());
        }

        if (updatedAddress.isDefault()) {
            addressRepository.resetDefaultForUser(userId);
        }

        updatedAddress.setDefault(addressUpdateRequestDTO.isDefault());
        return updatedAddress;
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found with id: " + userId));

        Address addressToDelete = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new NoResourceFoundException("Address not found"));

        boolean wasDefault = addressToDelete.isDefault();

        addressRepository.delete(addressToDelete);

        if (wasDefault) {
            addressRepository.findFirstByUserIdOrderByCreatedAtAsc(userId)
                    .ifPresent(oldest -> {
                        oldest.setDefault(true);
                        addressRepository.save(oldest);
                    });
        }
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long addressId, Long userId) {
        addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new NoResourceFoundException("Address not found"));

        addressRepository.resetDefaultForUser(userId);
        addressRepository.updateDefaultForUser(userId, addressId);
    }

    @Override
    @Transactional
    public List<Address> resolveAddresses(List<AddressRequestDTO> dto, User user) {
        if (dto == null || dto.isEmpty()) {
            return new ArrayList<>();
        }

        long defaultCount = dto.stream().filter(AddressRequestDTO::isDefault).count();
        if (defaultCount > 1) {
            throw new IllegalArgumentException("Only one default address allowed");
        }

        List<Address> addresses = new ArrayList<>();
        for (AddressRequestDTO request : dto) {
            Address newAddress = AddressMapper.toEntity(request);
            newAddress.setUser(user);
            addresses.add(newAddress);
        }

        return addresses;
    }
}
