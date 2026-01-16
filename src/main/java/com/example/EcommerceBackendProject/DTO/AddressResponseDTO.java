package com.example.EcommerceBackendProject.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("isDefault")
    private boolean isDefault;
}
