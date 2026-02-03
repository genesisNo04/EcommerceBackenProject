package com.example.EcommerceBackendProject.Mapper;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.AddressResponseDTO;
import com.example.EcommerceBackendProject.Entity.Address;

public class AddressMapper {
    public static AddressResponseDTO toDTO(Address address) {
        if (address == null) {
            return null;
        }

        AddressResponseDTO addressResponseDTO = new AddressResponseDTO();
        addressResponseDTO.setId(address.getId());
        addressResponseDTO.setStreet(address.getStreet());
        addressResponseDTO.setCity(address.getCity());
        addressResponseDTO.setState(address.getState());
        addressResponseDTO.setCountry(address.getCountry());
        addressResponseDTO.setZipCode(address.getZipCode());
        addressResponseDTO.setDefault(address.getIsDefault());
        addressResponseDTO.setUserId(address.getUser() != null ? address.getUser().getId() : null);

        return addressResponseDTO;
    }

    public static Address toEntity(AddressRequestDTO requestDTO) {
        Address newAddress = new Address();

        newAddress.setStreet(requestDTO.getStreet());
        newAddress.setCity(requestDTO.getCity());
        newAddress.setState(requestDTO.getState());
        newAddress.setCountry(requestDTO.getCountry());
        newAddress.setZipCode(requestDTO.getZipCode());
        newAddress.setIsDefault(requestDTO.getIsDefault());

        return newAddress;
    }
}
