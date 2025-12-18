package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.Entity.Address;

import java.util.List;
import java.util.Optional;

public interface AddressService {

    List<Address> getUserAddresses(Long userId);

    Address createAddress(Address address, Long userId);

    Address getDefaultAddress(Long userId);

    Address updateAddress(Long addressId, Address address, Long userId);

    void deleteAddress(Long addressId, Long userId);

    void setDefaultAddress(Long addressId, Long userId);
}
