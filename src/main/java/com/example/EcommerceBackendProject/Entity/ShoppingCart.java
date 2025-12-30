package com.example.EcommerceBackendProject.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = "user_id")
)
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "shoppingCart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShoppingCartItem> items = new HashSet<>();

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public ShoppingCart(User user) {
        this.user = user;
    }

    public void addItem(ShoppingCartItem item) {
        items.add(item);
        item.setShoppingCart(this);
    }

    public void removeItem(ShoppingCartItem item) {
        items.remove(item);
        item.setShoppingCart(null);
    }

    public void clearItems() {
        for (ShoppingCartItem item : items) {
            item.setShoppingCart(null);
        }
        items.clear();
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
