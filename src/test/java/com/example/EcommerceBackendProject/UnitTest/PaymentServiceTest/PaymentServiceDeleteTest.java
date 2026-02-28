package com.example.EcommerceBackendProject.UnitTest.PaymentServiceTest;

import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.PaymentTestUtils.createTestPayment;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceDeleteTest extends BasePaymentServiceTest{

    @Test
    void deletePayment() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);

        Payment payment = createTestPayment(order, PaymentType.CREDIT_CARD, PaymentStatus.INITIATED, "1234");
        order.setPayment(payment);
        payment.setOrder(order);

        when(paymentRepository.findByIdAndOrderUserId(1L, 1L)).thenReturn(Optional.of(payment));

        paymentService.deletePayment(1L, 1L);

        assertNull(order.getPayment());

        verify(paymentRepository).findByIdAndOrderUserId(1L, 1L);
        verify(paymentRepository).delete(payment);
    }

    @Test
    void deletePayment_paymentNotFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> paymentService.deletePayment(1L, 1L));

        assertEquals("No payment found", ex.getMessage());

        verify(paymentRepository).findByIdAndOrderUserId(1L, 1L);
        verify(paymentRepository, never()).delete(any(Payment.class));
    }
}
