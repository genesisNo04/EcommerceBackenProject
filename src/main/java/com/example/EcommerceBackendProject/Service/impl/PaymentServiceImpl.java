package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.Payment;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Enum.Status;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.NoUserFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Repository.OrderRepository;
import com.example.EcommerceBackendProject.Repository.PaymentRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Payment findPaymentByOrderIdAndUserId(Long orderId, Long userId) {
        return paymentRepository.findByOrderIdAndOrderUserId(orderId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Payment not found for this order."));
    }

    @Override
    public Page<Payment> findPaymentByStatusAndUserId(Status status, Long userId, Pageable pageable) {
        return paymentRepository.findByStatusAndOrderUserId(status, userId, pageable);
    }

    @Override
    public Page<Payment> findPaymentByPaymentTypeAndUserId(PaymentType paymentType, Long userId, Pageable pageable) {
        return paymentRepository.findByPaymentTypeAndOrderUserId(paymentType, userId, pageable);
    }

    @Override
    public Page<Payment> findPaymentByPaymentTypeAndStatusAndUserId(PaymentType paymentType, Status status, Long userId, Pageable pageable) {
        return paymentRepository.findByStatusAndPaymentTypeAndOrderUserId(status, paymentType, userId, pageable);
    }

    @Override
    public Page<Payment> findPaymentByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Long userId, Pageable pageable) {
        return paymentRepository.findByPaymentDateBetweenAndOrderUserId(start, end, userId, pageable);
    }

    @Override
    @Transactional
    public Payment createPayment(Payment payment, Long orderId, Long userId) {
        if(paymentRepository.findByOrderIdAndOrderUserId(orderId, userId).isPresent()) {
            throw new ResourceAlreadyExistsException("Payment already exists for this order");
        }

        userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("No user found"));
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Order not found!"));
        payment.setOrder(order);
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void deletePayment(Long paymentId, Long userId) {
        Payment payment = paymentRepository.findByIdAndOrderUserId(paymentId, userId)
                .orElseThrow(() -> new NoResourceFoundException("No payment found!"));
        paymentRepository.delete(payment);
    }

    @Override
    @Transactional
    public Payment updatePayment(Long paymentId, Payment updatedPayment, Long userId) {
        Payment payment = paymentRepository.findByIdAndOrderUserId(paymentId, userId)
                .orElseThrow(() -> new NoResourceFoundException("No payment found!"));
        payment.setPaymentType(updatedPayment.getPaymentType());
        payment.setAmount(updatedPayment.getAmount());
        return paymentRepository.save(payment);
    }

    @Override
    public Page<Payment> findPaymentByUserId(Long userId, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(() -> new NoUserFoundException("No user found!"));
        return paymentRepository.findByOrderUserId(userId, pageable);
    }
}
