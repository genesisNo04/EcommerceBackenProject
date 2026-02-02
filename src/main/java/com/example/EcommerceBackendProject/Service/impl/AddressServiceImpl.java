package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.AddressUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.NoUserFoundException;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import com.example.EcommerceBackendProject.Repository.AddressRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.AddressService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<Address> getUserAddresses(Long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found with id: " + userId));
        return addressRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public Address createAddress(AddressRequestDTO addressRequestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found with id: " + userId));
        Address address = AddressMapper.toEntity(addressRequestDTO);
        address.setUser(user);

        if (address.getIsDefault()) {
            addressRepository.resetDefaultForUser(userId);
        } else if (!addressRepository.existsByUserIdAndIsDefaultTrue(userId)) {
            address.setIsDefault(true);
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
    public Address updateAddress(Long addressId, AddressRequestDTO addressRequestDTO, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found with id: " + userId));

        Address updatedAddress = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new NoResourceFoundException("No address with this id: "+ addressId));

        return updateAddressInternally(updatedAddress, addressRequestDTO);
    }

    @Override
    @Transactional
    public Address patchAddress(Long addressId, AddressUpdateRequestDTO addressUpdateRequestDTO, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found with id: " + userId));

        Address updatedAddress = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new NoResourceFoundException("No address with this id: "+ addressId));

        return patchAddressInternally(updatedAddress, addressUpdateRequestDTO);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoUserFoundException("No user found with id: " + userId));

        Address addressToDelete = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new NoResourceFoundException("Address not found"));

        deleteAddressInternally(addressToDelete);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long addressId, Long userId) {
        Address address = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new NoResourceFoundException("Address not found"));

        setDefaultAddressInternally(address);
    }

    @Override
    @Transactional
    public List<Address> resolveAddresses(List<AddressRequestDTO> dto, User user) {
        if (dto == null || dto.isEmpty()) {
            return new ArrayList<>();
        }

        long defaultCount = dto.stream().filter(AddressRequestDTO::getIsDefault).count();
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

    @Override
    public Page<Address> findAllAddress(Pageable pageable) {
        return addressRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Address updateAnyAddress(Long addressId, AddressRequestDTO addressRequestDTO) {
        Address updatedAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new NoResourceFoundException("No address with this id: "+ addressId));

        return updateAddressInternally(updatedAddress, addressRequestDTO);
    }

    @Override
    public Address patchAnyAddress(Long addressId, AddressUpdateRequestDTO addressUpdateRequestDTO) {
        Address updatedAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new NoResourceFoundException("No address with this id: "+ addressId));

        return patchAddressInternally(updatedAddress, addressUpdateRequestDTO);
    }

    @Override
    public void deleteAnyAddress(Long addressId) {
        Address addressToDelete = addressRepository.findById(addressId)
                .orElseThrow(() -> new NoResourceFoundException("Address not found"));

        deleteAddressInternally(addressToDelete);
    }

    @Override
    @Transactional
    public void setDefaultAnyAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NoResourceFoundException("Address not found"));

        setDefaultAddressInternally(address);
    }

    @Transactional
    private Address updateAddressInternally(Address address, AddressRequestDTO addressRequestDTO) {
        if (addressRequestDTO.getIsDefault()) {
            addressRepository.resetDefaultForUser(address.getUser().getId());
        }

        address.setStreet(addressRequestDTO.getStreet());
        address.setState(addressRequestDTO.getState());
        address.setCity(addressRequestDTO.getCity());
        address.setCountry(addressRequestDTO.getCountry());
        address.setZipCode(addressRequestDTO.getZipCode());

        address.setIsDefault(addressRequestDTO.getIsDefault());
        return address;
    }

    @Transactional
    private Address patchAddressInternally(Address address, AddressUpdateRequestDTO addressUpdateRequestDTO) {

        boolean wasDefault = address.getIsDefault();

        if (addressUpdateRequestDTO.getIsDefault() != null) {
            address.setIsDefault(addressUpdateRequestDTO.getIsDefault());
        }

        if (wasDefault && Boolean.FALSE.equals(address.getIsDefault())) {
            promoteOldestIfNoDefault(address.getUser().getId());
        }


        if (addressUpdateRequestDTO.getStreet() != null) {
            address.setStreet(addressUpdateRequestDTO.getStreet());
        }

        if (addressUpdateRequestDTO.getState() != null) {
            address.setState(addressUpdateRequestDTO.getState());
        }

        if (addressUpdateRequestDTO.getCity() != null) {
            address.setCity(addressUpdateRequestDTO.getCity());
        }

        if (addressUpdateRequestDTO.getCountry() != null) {
            address.setCountry(addressUpdateRequestDTO.getCountry());
        }

        if (addressUpdateRequestDTO.getZipCode() != null) {
            address.setZipCode(addressUpdateRequestDTO.getZipCode());
        }

        return address;
    }

    @Transactional
    private void deleteAddressInternally(Address address) {
        boolean wasDefault = address.getIsDefault();

        addressRepository.delete(address);

        if (wasDefault) {
            promoteOldestIfNoDefault(address.getUser().getId());
        }
    }

    private void setDefaultAddressInternally(Address address) {
        Long userId = address.getUser().getId();
        addressRepository.resetDefaultForUser(userId);
        addressRepository.updateDefaultForUser(userId, address.getId());
    }

    private void promoteOldestIfNoDefault(Long userId) {
        addressRepository.findFirstByUserIdOrderByCreatedAtAsc(userId)
                .ifPresent(oldest -> {
                    oldest.setIsDefault(true);
                    addressRepository.save(oldest);
                });
    }
}
