package com.example.EcommerceBackendProject.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @JsonProperty("isDefault")
    private Boolean isDefault;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public Address(User user, String street, String city, String state, String country, String zipCode, Boolean isDefault) {
        this.user = user;
        this.street = street;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipCode = zipCode;
        this.isDefault = isDefault;
    }

    @PrePersist
    public void createAt() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void modifiedAt() {
        this.modifiedAt = LocalDateTime.now();
    }

}
