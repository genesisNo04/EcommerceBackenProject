package com.example.EcommerceBackendProject.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}),
        indexes = {
            @Index(name = "idx_review_user", columnList = "user_id"),
            @Index(name = "idx_review_product", columnList = "product_id"),
            @Index(name = "idx_review_rating", columnList = "rating"),
            @Index(name = "idx_review_product_rating", columnList = "product_id, rating")
        }
)
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int rating;

    private String comment;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    @PrePersist
    private void createdAt() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    public Review(int rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    @PreUpdate
    private void modifiedAt() {
        this.modifiedAt = LocalDateTime.now();
    }
}
