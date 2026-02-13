package com.example.EcommerceBackendProject.UnitTest.Utilities;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;

public class AddressServiceUtils {

    public static AddressRequestDTO createAddressDto(String street, String city, String state, String country, String zipcode, Boolean isDefault) {
        return new AddressRequestDTO(street, city, state, country, zipcode, isDefault);
    }
}
