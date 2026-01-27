package com.example.EcommerceBackendProject.Entity.Payment;

import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = "order_id"),
        indexes = {
                @Index(name = "id_payment_order", columnList = "order_id"),
                @Index(name = "id_payment_status", columnList = "status"),
                @Index(name = "id_payment_type", columnList = "paymentType"),
                @Index(name = "id_payment_date", columnList = "createdAt")
        }
)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType paymentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private String providerReference;

    @PrePersist
    protected void onCreated() {
        this.createdAt = LocalDateTime.now();
    }

    public Payment(Order order, PaymentType paymentType) {
        this.order = order;
        this.paymentType = paymentType;
        this.status = PaymentStatus.INITIATED;
    }

    public static Payment createPayment(Order order, PaymentType type, String providerReference, PaymentStatus paymentStatus) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentType(type);
        payment.setAmount(order.getTotalAmount());
        payment.setProviderReference(providerReference);
        payment.setStatus(paymentStatus);
        return payment;
    }

    public void assignTo(Order order) {
        this.order = order;
    }
}
