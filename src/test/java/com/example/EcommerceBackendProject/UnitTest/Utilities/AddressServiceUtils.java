package com.example.EcommerceBackendProject.UnitTest.Utilities;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.AddressUpdateRequestDTO;
import com.example.EcommerceBackendProject.Entity.Address;
import com.example.EcommerceBackendProject.Mapper.AddressMapper;

public class AddressServiceUtils {

    public static AddressRequestDTO createAddressDto(String street, String city, String state, String country, String zipcode, Boolean isDefault) {
        return new AddressRequestDTO(street, city, state, country, zipcode, isDefault);
    }

    public static AddressUpdateRequestDTO createUpdateAddressDto(String street, String city, String state, String country, String zipcode, Boolean isDefault) {
        return new AddressUpdateRequestDTO(street, city, state, country, zipcode, isDefault);
    }

    public static Address createAddress(String street, String city, String state, String country, String zipcode, Boolean isDefault) {
        AddressRequestDTO addressRequestDTO = createAddressDto(street, city, state, country, zipcode, isDefault);
        return AddressMapper.toEntity(addressRequestDTO);
    }
}
