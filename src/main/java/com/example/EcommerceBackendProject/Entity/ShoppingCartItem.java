package com.example.EcommerceBackendProject.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"})
)
public class ShoppingCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal priceSnapshot;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_id")
    private ShoppingCart shoppingCart;

    public ShoppingCartItem(Product product, int quantity, BigDecimal priceSnapshot, ShoppingCart shoppingCart) {
        this.product = product;
        this.quantity = quantity;
        this.priceSnapshot = priceSnapshot;
        this.shoppingCart = shoppingCart;
    }
}
