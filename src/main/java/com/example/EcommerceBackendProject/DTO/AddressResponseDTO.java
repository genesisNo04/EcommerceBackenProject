package com.example.EcommerceBackendProject.DTO;

import com.example.EcommerceBackendProject.Entity.Address;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddressResponseDTO {

    private Long id;

    private Long userId;

    private String street;

    private String city;

    private String state;

    private String country;

    private String zipCode;

    private boolean isDefault;
}
