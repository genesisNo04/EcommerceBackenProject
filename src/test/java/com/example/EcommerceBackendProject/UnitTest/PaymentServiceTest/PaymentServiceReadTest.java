package com.example.EcommerceBackendProject.UnitTest.PaymentServiceTest;

import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.PaymentTestUtils.createTestPayment;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceReadTest extends BasePaymentServiceTest {

    @Test
    void findPayment_byOrderIdAndUserId() {
        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(OrderStatus.PENDING_PAYMENT);

        Payment payment = createTestPayment(order, PaymentType.CREDIT_CARD, PaymentStatus.INITIATED, "1234");
        when(paymentRepository.findByOrderIdAndOrderUserId(1L, 1L)).thenReturn(Optional.of(payment));

        Payment searchPayment = paymentService.findPaymentByOrderIdAndUserId(1L, 1L);

        assertEquals(PaymentType.CREDIT_CARD, searchPayment.getPaymentType());
        assertEquals(PaymentStatus.INITIATED, searchPayment.getStatus());
        assertEquals("1234", searchPayment.getProviderReference());

        verify(paymentRepository).findByOrderIdAndOrderUserId(1L, 1L);
    }

    @Test
    void findPayment_noPaymentFound() {
        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> paymentService.findPaymentByOrderIdAndUserId(1L, 1L));

        assertEquals("Payment not found for this order", ex.getMessage());

        verify(paymentRepository).findByOrderIdAndOrderUserId(1L, 1L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findPayment_withFilter() {
        Order order = new Order();

        Payment payment = createTestPayment(order, PaymentType.CREDIT_CARD, PaymentStatus.INITIATED, "1234");
        Payment payment1 = createTestPayment(order, PaymentType.CREDIT_CARD, PaymentStatus.INITIATED, "1235");
        Payment payment2 = createTestPayment(order, PaymentType.CREDIT_CARD, PaymentStatus.INITIATED, "1236");
        List<Payment> paymentList = List.of(payment, payment1, payment2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Payment> payments = new PageImpl<>(paymentList, pageable, 10);

        when(paymentRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(payments);

        Page<Payment> returnPayment = paymentService.findPayments(1L, 1L, PaymentStatus.INITIATED, PaymentType.CREDIT_CARD, LocalDateTime.now().minusDays(10), LocalDateTime.now(), pageable);

        assertEquals(10, returnPayment.getTotalElements());
        assertEquals(3, returnPayment.getContent().size());
        assertTrue(returnPayment.getContent().containsAll(paymentList));

        verify(paymentRepository).findAll(any(Specification.class), eq(pageable));
    }
}
