package com.example.EcommerceBackendProject.Service;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.AddressUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface AddressService {

    Page<Address> getUserAddresses(Long userId, Pageable pageable);

    Address createAddress(AddressRequestDTO addressRequestDTO, Long userId);

    Address getDefaultAddress(Long userId);

    Address updateAddress(Long addressId, AddressRequestDTO addressRequestDTO, Long userId);

    Address patchAddress(Long addressId, AddressUpdateRequestDTO addressUpdateRequestDTO, Long userId);

    void deleteAddress(Long addressId, Long userId);

    void setDefaultAddress(Long addressId, Long userId);

    List<Address> resolveAddresses(List<AddressRequestDTO> dto, User user);

    //Admin service
    Page<Address> findAllAddress(Pageable pageable);

    Address updateAnyAddress(Long addressId, AddressRequestDTO addressRequestDTO);

    Address patchAnyAddress(Long addressId, AddressUpdateRequestDTO addressUpdateRequestDTO);

    void deleteAnyAddress(Long addressId);

    void setDefaultAnyAddress(Long addressId);
}
