package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.NoUserFoundException;
import com.example.EcommerceBackendProject.Repository.AddressRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.AddressService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Address> getUserAddresses(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoUserFoundException("No user found with id: " + userId);
        }
        return addressRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Address createAddress(Address address, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found with id: " + userId));

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
        if (!userRepository.existsById(userId)) {
            throw new NoUserFoundException("No user found with id: " + userId);
        }

        return addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new NoResourceFoundException("User does not have a default address"));
    }

    @Override
    @Transactional
    public Address updateAddress(Long addressId, Address address, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoUserFoundException("No user found with id: " + userId);
        }

        Address updatedAddress = addressRepository.findByUserIdAndId(userId, addressId).orElseThrow(() -> new NoResourceFoundException("No address with this id: "+ addressId));

        if (address.isDefault()) {
            addressRepository.resetDefaultForUser(userId);
        }

        updatedAddress.setStreet(address.getStreet());
        updatedAddress.setState(address.getState());
        updatedAddress.setCity(address.getCity());
        updatedAddress.setCountry(address.getCountry());
        updatedAddress.setZipCode(address.getZipCode());
        updatedAddress.setDefault(address.isDefault());
        addressRepository.save(updatedAddress);
        return updatedAddress;
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoUserFoundException("No user found with id: " + userId);
        }

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

        Address address = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new NoResourceFoundException("Address not found"));

        addressRepository.resetDefaultForUser(userId);
        addressRepository.updateDefaultForUser(userId, addressId);
    }
}
