package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;

import java.util.List;
import java.util.Optional;

public interface AddressService {

    List<Address> getUserAddresses(Long userId);

    Address createAddress(AddressRequestDTO addressRequestDTO, Long userId);

    Address getDefaultAddress(Long userId);

    Address updateAddress(Long addressId, AddressRequestDTO addressRequestDTO, Long userId);

    void deleteAddress(Long addressId, Long userId);

    void setDefaultAddress(Long addressId, Long userId);

    List<Address> resolveAddresses(List<AddressRequestDTO> dto, User user);
}
