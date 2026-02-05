package com.example.EcommerceBackendProject.Service.impl;

import com.example.EcommerceBackendProject.DTO.PaymentRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Entity.Payment.PaymentGateway;
import com.example.EcommerceBackendProject.Entity.Payment.PaymentRequest;
import com.example.EcommerceBackendProject.Entity.Payment.PaymentResult;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Exception.InvalidPaymentStatusException;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.Repository.OrderRepository;
import com.example.EcommerceBackendProject.Repository.PaymentRepository;
import com.example.EcommerceBackendProject.Service.PaymentService;
import com.example.EcommerceBackendProject.Specification.PaymentSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final OrderRepository orderRepository;

    private final PaymentGateway paymentGateway;

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository, PaymentGateway paymentGateway) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway;
    }

    @Override
    public Payment findPaymentByOrderIdAndUserId(Long orderId, Long userId) {
        Payment payment = paymentRepository.findByOrderIdAndOrderUserId(orderId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Payment not found for this order."));
        log.info("FETCHED payment [paymentId={}] for order [orderId={}]", payment.getId(), orderId);
        return payment;
    }

    @Override
    @Transactional
    public void deletePayment(Long paymentId, Long userId) {
        Payment payment = paymentRepository.findByIdAndOrderUserId(paymentId, userId)
                .orElseThrow(() -> new NoResourceFoundException("No payment found!"));

        Order order = payment.getOrder();

        if (order != null) {
            order.setPayment(null);
            payment.setOrder(null);
        }

        log.info("DELETED payment [paymentId={}]", paymentId);
        paymentRepository.delete(payment);
    }

    @Override
    @Transactional
    public Payment updatePayment(Long paymentId, PaymentRequestDTO paymentRequestDTO, Long userId) {
        Payment payment = paymentRepository.findByIdAndOrderUserId(paymentId, userId)
                .orElseThrow(() -> new NoResourceFoundException("No payment found!"));

        if (payment.getStatus() != PaymentStatus.INITIATED) {
            throw new InvalidPaymentStatusException("Only INITIATED payments can be updated");
        }

        Order order = payment.getOrder();
        payment.setPaymentType(paymentRequestDTO.getPaymentType());
        payment.setAmount(order.getTotalAmount());

        log.info("UPDATED payment [paymentId={}]", paymentId);

        return paymentRepository.save(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Payment> findPayments(Long userId, Long orderId, PaymentStatus status, PaymentType paymentType, LocalDateTime start, LocalDateTime end, Pageable pageable) {

        Specification<Payment> spec = (root, query, cb) -> cb.conjunction();

        if (userId != null) {
            spec = spec.and(PaymentSpecification.hasUserId(userId));
        }

        if (orderId != null) {
            spec = spec.and(PaymentSpecification.hasOrderId(orderId));
        }

        if (status != null) {
            spec = spec.and(PaymentSpecification.hasStatus(status));
        }

        if (paymentType != null) {
            spec = spec.and(PaymentSpecification.hasPaymentType(paymentType));
        }

        if (start != null || end != null) {
            spec = spec.and(PaymentSpecification.createdBetween(start, end));
        }

        Page<Payment> payments = paymentRepository.findAll(spec, pageable);
        log.info("FETCHED payments [total={}]", payments.getTotalElements());

        return payments;
    }

    @Override
    @Transactional
    public Payment processPayment(Long orderId, Long userId, PaymentType paymentType) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new NoResourceFoundException("Order not found"));

        if (order.getOrderStatus() == OrderStatus.PAID) {
            return order.getPayment();
        }

        if (order.getOrderStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order is not payable");
        }

        PaymentResult result = paymentGateway.charge(new PaymentRequest(order.getId(), order.getTotalAmount()));

        Payment payment;

        if (result.isSuccess()) {
            order.markPaid();
            payment = Payment.createPayment(order, paymentType, result.getReferenceId(), PaymentStatus.AUTHORIZED);
            order.attachPayment(payment);
            paymentRepository.save(payment);
        } else {
            order.markFailed();
            payment = Payment.createPayment(order, paymentType, result.getReferenceId(), PaymentStatus.FAILED);
            order.attachPayment(payment);
            paymentRepository.save(payment);
        }

        log.info("PROCESSED payment [paymentId={}] [status={}] for order [orderId={}] user [targetUserId={}]", payment.getId(), payment.getStatus(), orderId, userId);

        return payment;
    }
}
