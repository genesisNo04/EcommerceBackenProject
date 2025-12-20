package com.example.EcommerceBackendProject.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String street;

    private String city;

    private String state;

    private String country;

    private String zipCode;

    private boolean isDefault;

    public Address(User user, String street, String city, String state, String country, String zipCode, boolean isDefault) {
        this.user = user;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipCode = zipCode;
        this.isDefault = isDefault;
    }
}
