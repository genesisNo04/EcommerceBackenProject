package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.AddressUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;
import com.example.EcommerceBackendProject.Repository.AddressRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Security.SecurityUtils;
import com.example.EcommerceBackendProject.Service.AddressService;
import com.example.EcommerceBackendProject.Utilities.LoggingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AddressServiceImpl implements AddressService {

    private static final Logger log = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<Address> getUserAddresses(Long userId, Pageable pageable) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("No user found with id: " + userId));

        log.info("FETCHED address for [targetUserId={}]", userId);

        return addressRepository.findByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public Address createAddress(AddressRequestDTO addressRequestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("No user found with id: " + userId));
        Address address = AddressMapper.toEntity(addressRequestDTO);
        address.setUser(user);

        if (address.getIsDefault()) {
            addressRepository.resetDefaultForUser(userId);
            log.info("RESET default address for user [targetUserId={}]", userId);
        } else if (!addressRepository.existsByUserIdAndIsDefaultTrue(userId)) {
            address.setIsDefault(true);
            log.info("PROMOTED address as default for user [targetUserId={}]", userId);
        }

        Address saved = addressRepository.save(address);

        log.info("CREATED address [addressId={}] for user [targetUserId={}] default={}", saved.getId(), userId, saved.getIsDefault());

        return saved;
    }


    @Override
    public Address getDefaultAddress(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("No user found with id: " + userId));

        log.info("FETCHED for default address for [targetUserId={}]", userId);

        return addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new NoResourceFoundException("User does not have a default address"));
    }

    @Override
    @Transactional
    public Address updateAddress(Long addressId, AddressRequestDTO addressRequestDTO, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("No user found with id: " + userId));

        Address updatedAddress = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new NoResourceFoundException("No address with this id: "+ addressId));

        log.info("UPDATED address [addressId={}] for [targetUserId={}]", updatedAddress.getId(), userId);

        return updateAddressInternally(updatedAddress, addressRequestDTO);
    }

    @Override
    @Transactional
    public Address patchAddress(Long addressId, AddressUpdateRequestDTO addressUpdateRequestDTO, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("No user found with id: " + userId));

        Address updatedAddress = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new NoResourceFoundException("No address with this id: "+ addressId));

        log.info("PATCHED address for [targetUserId={}]", userId);

        return patchAddressInternally(updatedAddress, addressUpdateRequestDTO);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NoResourceFoundException("No user found with id: " + userId));

        Address addressToDelete = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new NoResourceFoundException("Address not found"));

        log.info("DELETED address for [targetUserId={}]", userId);

        deleteAddressInternally(addressToDelete);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long addressId, Long userId) {
        Address address = addressRepository.findByUserIdAndId(userId, addressId)
                .orElseThrow(() -> new NoResourceFoundException("Address not found"));

        setDefaultAddressInternally(address);

        log.info("ASSIGNED default address for [targetUserId={}]", userId);
    }

    @Override
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
        log.info("FETCH addresses");

        return addressRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public Address updateAnyAddress(Long addressId, AddressRequestDTO addressRequestDTO) {
        Address updatedAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new NoResourceFoundException("No address with this id: "+ addressId));

        log.info("UPDATED address for [addressId={}] ", addressId);

        return updateAddressInternally(updatedAddress, addressRequestDTO);
    }

    @Override
    public Address patchAnyAddress(Long addressId, AddressUpdateRequestDTO addressUpdateRequestDTO) {
        Address updatedAddress = addressRepository.findById(addressId)
                .orElseThrow(() -> new NoResourceFoundException("No address with this id: "+ addressId));

        log.info("PATCHED address for [addressId={}]", addressId);

        return patchAddressInternally(updatedAddress, addressUpdateRequestDTO);
    }

    @Override
    public void deleteAnyAddress(Long addressId) {
        Address addressToDelete = addressRepository.findById(addressId)
                .orElseThrow(() -> new NoResourceFoundException("Address not found"));

        log.info("DELETED address [addressId={}]", addressId);

        deleteAddressInternally(addressToDelete);
    }

    @Override
    @Transactional
    public void setDefaultAnyAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NoResourceFoundException("Address not found"));

        log.info("SET default address to [addressId={}]", addressId);

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

        if (!wasDefault && Boolean.TRUE.equals(addressUpdateRequestDTO.getIsDefault())) {
            addressRepository.resetDefaultForUser(address.getUser().getId());
            address.setIsDefault(addressUpdateRequestDTO.getIsDefault());
        } else if (wasDefault && Boolean.FALSE.equals(addressUpdateRequestDTO.getIsDefault())) {
            address.setIsDefault(false);
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
