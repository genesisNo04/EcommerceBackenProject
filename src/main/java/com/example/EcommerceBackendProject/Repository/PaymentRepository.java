package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.Payment;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Enum.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(long orderId);

    List<Payment> findByStatus(Status status);

    List<Payment> findByPaymentType(PaymentType type, Pageable pageable);

    List<Payment> findByStatusAndPaymentType(Status status, PaymentType type, Pageable pageable);

    List<Payment> findByPaymentDateBetween(LocalDateTime start, LocalDateTime end);
}
