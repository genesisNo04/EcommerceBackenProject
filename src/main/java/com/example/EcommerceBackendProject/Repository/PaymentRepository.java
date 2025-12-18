package com.example.EcommerceBackendProject.Repository;

import com.example.EcommerceBackendProject.Entity.Payment;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Enum.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderIdAndOrderUserId(long orderId, Long userid);

    Page<Payment> findByStatusAndOrderUserId(Status status, Long userid, Pageable pageable);

    Page<Payment> findByPaymentTypeAndOrderUserId(PaymentType type, Long userid, Pageable pageable);

    Page<Payment> findByStatusAndPaymentTypeAndOrderUserId(Status status, Long userid, PaymentType type, Pageable pageable);

    List<Payment> findByPaymentDateBetweenAndOrderUserId(LocalDateTime start, LocalDateTime end, Long userid, Pageable pageable);
}
