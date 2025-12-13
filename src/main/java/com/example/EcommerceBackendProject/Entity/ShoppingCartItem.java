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
public class ShoppingCartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

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
