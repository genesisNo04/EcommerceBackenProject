package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.PaymentRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.Payment;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Exception.InvalidPaymentStatusException;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Exception.ResourceAlreadyExistsException;
import com.example.EcommerceBackendProject.Repository.OrderRepository;
import com.example.EcommerceBackendProject.Repository.PaymentRepository;
import com.example.EcommerceBackendProject.Repository.UserRepository;
import com.example.EcommerceBackendProject.Service.PaymentService;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public Payment createPayment(PaymentRequestDTO paymentRequestDTO, Long userId) {
        if(paymentRepository.findByOrderIdAndOrderUserId(paymentRequestDTO.getOrderId(), userId).isPresent()) {
            throw new ResourceAlreadyExistsException("Payment already exists for this order");
        }

        Order order = orderRepository.findByIdAndUserId(paymentRequestDTO.getOrderId(), userId)
                .orElseThrow(() -> new NoResourceFoundException("Order not found!"));
        Payment payment = Payment.createPayment(order, paymentRequestDTO.getPaymentType());
        order.setPayment(payment);

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
    public Payment updatePayment(Long paymentId, PaymentRequestDTO paymentRequestDTO, Long userId) {
        Payment payment = paymentRepository.findByIdAndOrderUserId(paymentId, userId)
                .orElseThrow(() -> new NoResourceFoundException("No payment found!"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new InvalidPaymentStatusException("Only PENDING payments can be updated");
        }

        Order order = payment.getOrder();
        payment.setPaymentType(paymentRequestDTO.getPaymentType());
        payment.setAmount(order.getTotalAmount());

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Payment> findPayments(Long userId, PaymentStatus status, PaymentType paymentType, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Page<Payment> payment;

        if (status != null && paymentType != null && (start != null || end != null)) {
            payment = paymentRepository.findByStatusAndPaymentTypeAndOrderUserIdAndCreatedAtBetween(status, paymentType, userId, start, end, pageable);
        } else if (start != null || end != null) {
            payment = paymentRepository.findByCreatedAtBetweenAndOrderUserId(start, end, userId, pageable);
        } else if (status != null && paymentType != null) {
            payment = paymentRepository.findByStatusAndPaymentTypeAndOrderUserId(status, paymentType, userId, pageable);
        } else if (status != null) {
            payment = paymentRepository.findByStatusAndOrderUserId(status, userId, pageable);
        } else if (paymentType != null) {
            payment = paymentRepository.findByPaymentTypeAndOrderUserId(paymentType, userId, pageable);
        } else {
            payment = paymentRepository.findByOrderUserId(userId, pageable);
        }

        return payment;
    }
}
