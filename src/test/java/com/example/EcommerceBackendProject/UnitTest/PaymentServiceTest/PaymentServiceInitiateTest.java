package com.example.EcommerceBackendProject.UnitTest.PaymentServiceTest;

import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PaymentServiceInitiateTest extends BasePaymentServiceTest {

    @Test
    void initiatePayment() {
        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(BigDecimal.valueOf(1000));
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Payment payment = paymentService.initiatePayment(1L, 1L, PaymentType.CREDIT_CARD);

        assertEquals(PaymentStatus.INITIATED, payment.getStatus());
        assertEquals(PaymentType.CREDIT_CARD, payment.getPaymentType());
        assertSame(order, payment.getOrder());
        assertSame(payment, order.getPayment());
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getOrderStatus());
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    void initiatePayment_failed_orderNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> paymentService.initiatePayment(1L, 1L, PaymentType.CREDIT_CARD));

        assertEquals("No order found", ex.getMessage());
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    void initiatePayment_failed_paymentExist() {
        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(BigDecimal.valueOf(1000));
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Payment payment = paymentService.initiatePayment(1L, 1L, PaymentType.CREDIT_CARD);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> paymentService.initiatePayment(1L, 1L, PaymentType.CREDIT_CARD));

        assertEquals("Payment already exist", ex.getMessage());
        verifyNoMoreInteractions(paymentRepository);
    }
}
