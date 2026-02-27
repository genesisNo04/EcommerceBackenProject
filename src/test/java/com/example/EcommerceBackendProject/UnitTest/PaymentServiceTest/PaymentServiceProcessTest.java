package com.example.EcommerceBackendProject.UnitTest.PaymentServiceTest;

import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Entity.Payment.PaymentRequest;
import com.example.EcommerceBackendProject.Entity.Payment.PaymentResult;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.PaymentTestUtils.createTestPayment;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceProcessTest extends  BasePaymentServiceTest {

    @Test
    void processPayment() {
        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(BigDecimal.valueOf(1000));
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));
        when(paymentGateway.charge(any(PaymentRequest.class))).thenReturn(new PaymentResult(true, "1234"));

        Payment payment = paymentService.processPayment(1L, 1L, PaymentType.CREDIT_CARD);

        assertEquals(PaymentStatus.AUTHORIZED, payment.getStatus());
        assertEquals(PaymentType.CREDIT_CARD, payment.getPaymentType());
        assertSame(order, payment.getOrder());
        assertSame(payment, order.getPayment());
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        assertEquals("1234", payment.getProviderReference());

        ArgumentCaptor<PaymentRequest> captor = ArgumentCaptor.forClass(PaymentRequest.class);
        verify(paymentGateway).charge(captor.capture());

        PaymentRequest request = captor.getValue();
        assertEquals(1L, request.getOrderId());
        assertEquals(order.getTotalAmount(), request.getAmount());

        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(paymentRepository).save(any(Payment.class));
        verifyNoMoreInteractions(orderRepository, paymentGateway, paymentRepository);
    }

    @Test
    void processPayment_failedPayment() {
        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(BigDecimal.valueOf(1000));
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));
        when(paymentGateway.charge(any(PaymentRequest.class))).thenReturn(new PaymentResult(false, "1234"));

        Payment payment = paymentService.processPayment(1L, 1L, PaymentType.CREDIT_CARD);

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
        assertEquals(PaymentType.CREDIT_CARD, payment.getPaymentType());
        assertSame(order, payment.getOrder());
        assertSame(payment, order.getPayment());
        assertEquals(OrderStatus.FAILED, order.getOrderStatus());
        assertEquals("1234", payment.getProviderReference());

        ArgumentCaptor<PaymentRequest> captor = ArgumentCaptor.forClass(PaymentRequest.class);
        verify(paymentGateway).charge(captor.capture());

        PaymentRequest request = captor.getValue();
        assertEquals(1L, request.getOrderId());
        assertEquals(order.getTotalAmount(), request.getAmount());

        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(paymentRepository).save(any(Payment.class));
        verifyNoMoreInteractions(orderRepository, paymentGateway, paymentRepository);
    }

    @Test
    void processPayment_orderNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> paymentService.processPayment(1L, 1L, PaymentType.CREDIT_CARD));

        assertEquals("No order found", ex.getMessage());

        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentGateway, never()).charge(any(PaymentRequest.class));
        verifyNoMoreInteractions(orderRepository, paymentGateway, paymentRepository);
    }

    @Test
    void processPayment_resubmitPayment() {
        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(BigDecimal.valueOf(1000));
        order.setOrderStatus(OrderStatus.PAID);

        Payment initialPayment = createTestPayment(order, PaymentType.CREDIT_CARD, PaymentStatus.INITIATED, "1234");
        order.setPayment(initialPayment);

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        Payment payment = paymentService.processPayment(1L, 1L, PaymentType.CREDIT_CARD);

        assertSame(initialPayment, payment);
        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentGateway, never()).charge(any(PaymentRequest.class));
        verifyNoMoreInteractions(orderRepository, paymentGateway, paymentRepository);
    }

    @Test
    void processPayment_orderNotInPendingState() {
        Order order = new Order();
        order.setId(1L);
        order.setTotalAmount(BigDecimal.valueOf(1000));
        order.setOrderStatus(OrderStatus.CREATED);

        Payment initialPayment = createTestPayment(order, PaymentType.CREDIT_CARD, PaymentStatus.INITIATED, "1234");
        order.setPayment(initialPayment);

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> paymentService.processPayment(1L, 1L, PaymentType.CREDIT_CARD));

        assertEquals("Order is not payable", ex.getMessage());

        verify(orderRepository).findByIdAndUserId(1L, 1L);
        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentGateway, never()).charge(any(PaymentRequest.class));
        verifyNoMoreInteractions(orderRepository, paymentGateway, paymentRepository);
    }


}
