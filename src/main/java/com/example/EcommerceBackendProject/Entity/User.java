package com.example.EcommerceBackendProject.Entity;

import com.example.EcommerceBackendProject.Enum.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;

    private String lastName;

    @OneToMany(mappedBy = "user")
    private Address address;

    private String phoneNumber;

    //ElementCollection: tell spring this is a collection of basic or embeddable types, not an entity
    //JPA will create a separate join table to store the list of roles per user
    @ElementCollection(fetch = FetchType.EAGER)
    //This tell spring how to store the enum, there are 2 type, first is string and ordinal (index of ENUM): 0,1,2
    @Enumerated(EnumType.STRING)
    private List<Role> roles;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ShoppingCart cart;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orderList;

    public User(ShoppingCart cart, List<Role> roles, String phoneNumber, Address address, String lastName, String firstName, String password, String email, String username) {
        this.cart = cart;
        this.roles = roles;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.lastName = lastName;
        this.firstName = firstName;
        this.password = password;
        this.email = email;
        this.username = username;
    }

    @PrePersist
    private void createdAt() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void modifiedAt() {
        this.modifiedAt = LocalDateTime.now();
    }
}
