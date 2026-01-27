package com.example.EcommerceBackendProject.Entity;

import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table( name = "user_order",
        indexes = {
                @Index(name = "idx_order_user", columnList = "user_id"),
                @Index(name = "idx_order_status", columnList = "orderStatus"),
                @Index(name = "idx_order_created", columnList = "createdAt"),
        }
)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Version
    private Long version;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus orderStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderItem> orderItems = new HashSet<>();

    @OneToOne(mappedBy = "order")
    private Payment payment;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public Order(User user) {
        this.user = user;
        this.orderStatus = OrderStatus.CREATED;
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

    public void attachPayment(Payment payment) {
        if (this.payment != null) {
            throw new IllegalStateException("Order already have a payment");
        }
        this.payment = payment;
        payment.assignTo(this);
    }

    public void markPendingPayment() {
        if (orderStatus != OrderStatus.CREATED) {
            throw new IllegalStateException("Order cannot move to PENDING_PAYMENT from " + orderStatus);
        }

        this.orderStatus = OrderStatus.PENDING_PAYMENT;
    }

    public void markPaid() {
        if (orderStatus != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order cannot move to PAID from " + orderStatus);
        }

        this.orderStatus = OrderStatus.PAID;
    }

    public void markFailed() {
        if (orderStatus != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order cannot move to FAILED from " + orderStatus);
        }

        this.orderStatus = OrderStatus.FAILED;
    }

    public void markCanceled() {
        if (orderStatus != OrderStatus.PENDING_PAYMENT && orderStatus != OrderStatus.CREATED ) {
            throw new IllegalStateException("Order cannot move to CANCELED from " + orderStatus);
        }

        this.orderStatus = OrderStatus.CANCELLED;
    }
}
