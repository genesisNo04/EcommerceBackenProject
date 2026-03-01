package com.example.EcommerceBackendProject.IntegrationTesting.Utilities;

import com.example.EcommerceBackendProject.DTO.AddressRequestDTO;

import java.util.Random;

public final class AddressTestFactory {

    public static AddressRequestDTO createAddress(boolean isDefault) {
        Random random = new Random();
        return new AddressRequestDTO(
                random.nextInt() + " Main st",
                "Sacramento",
                "CA",
                "USA",
                "12345",
                isDefault);
    }
}
