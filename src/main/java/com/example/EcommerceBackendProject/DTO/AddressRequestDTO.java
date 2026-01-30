package com.example.EcommerceBackendProject.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressRequestDTO {

    @NotBlank(message = "Street cannot be empty")
    private String street;

    @NotBlank(message = "City cannot be empty")
    private String city;

    @NotBlank(message = "State cannot be empty")
    private String state;

    @NotBlank(message = "Country cannot be empty")
    private String country;

    @NotBlank(message = "Zipcode cannot be empty")
    //Zip code can be 5 digit and next 4 digit optionally
    //? mean 0 or more occurrence
    @Pattern(regexp = "\\d{5}(-\\d{4})?", message = "Zipcode must be 5 digits")
    private String zipCode;

    @JsonProperty("isDefault")
    @NotNull
    private Boolean isDefault;

}
