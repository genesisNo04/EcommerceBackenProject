package com.example.EcommerceBackendProject.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressUpdateRequestDTO {

    private String street;

    private String city;

    private String state;

    private String country;

    //Zip code can be 5 digit and next 4 digit optionally
    //? mean 0 or more occurrence
    @Pattern(regexp = "\\d{5}(-\\d{4})?", message = "Zipcode must be 5 digits")
    private String zipCode;

    @JsonProperty("isDefault")
    private boolean isDefault;
}
