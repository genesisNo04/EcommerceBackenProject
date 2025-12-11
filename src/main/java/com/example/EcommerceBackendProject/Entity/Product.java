package com.example.EcommerceBackendProject.Entity;

import com.example.EcommerceBackendProject.Enum.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String productName;

    private String description;

    private double price;

    private int stockQuantity;

    private List<Category> categoryList;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public Product(String productName, String description, double price, int stockQuantity, List<Category> categoryList) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryList = categoryList;
    }
}
