package com.example.EcommerceBackendProject.IntegrationTesting.Utilities;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;
import com.example.EcommerceBackendProject.DTO.AddressUpdateRequestDTO;

import java.util.Random;

public final class AddressTestFactory {

    public static AddressRequestDTO createAddress(Boolean isDefault) {
        Random random = new Random();
        return new AddressRequestDTO(
                random.nextInt() + " Main st",
                "Sacramento",
                "CA",
                "USA",
                "12345",
                isDefault);
    }

    public static AddressRequestDTO createAddress(String street, String city, String state, String country, String zipcode, Boolean isDefault) {
        return new AddressRequestDTO(
                street,
                city,
                state,
                country,
                zipcode,
                isDefault);
    }

    public static AddressUpdateRequestDTO createUpdateAddress(String street, String city, String state, String country, String zipcode, Boolean isDefault) {
        return new AddressUpdateRequestDTO(
                street,
                city,
                state,
                country,
                zipcode,
                isDefault);
    }
}
