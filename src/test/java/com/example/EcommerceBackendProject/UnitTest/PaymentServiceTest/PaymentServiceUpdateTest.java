package com.example.EcommerceBackendProject.UnitTest.PaymentServiceTest;

import com.example.EcommerceBackendProject.DTO.PaymentRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Exception.InvalidPaymentStatusException;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static com.example.EcommerceBackendProject.UnitTest.Utilities.PaymentTestUtils.createTestPayment;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentServiceUpdateTest extends BasePaymentServiceTest {

    @Test
    void updatePayment() {
        Order order = new Order();
        Payment payment = createTestPayment(order, PaymentType.CREDIT_CARD, PaymentStatus.INITIATED, "1234");

        PaymentRequestDTO paymentRequestDTO = createTestPayment(1L, PaymentType.DEBIT_CARD);

        when(paymentRepository.findByIdAndOrderUserId(1L, 1L)).thenReturn(Optional.of(payment));

        Payment updatePayment = paymentService.updatePayment(1L, paymentRequestDTO, 1L);

        assertEquals(PaymentType.DEBIT_CARD, updatePayment.getPaymentType());
        assertEquals(PaymentStatus.INITIATED, updatePayment.getStatus());
        assertEquals("1234", updatePayment.getProviderReference());
        assertSame(order, updatePayment.getOrder());

        verify(paymentRepository).findByIdAndOrderUserId(1L, 1L);
    }

    @Test
    void updatePayment_paymentNotFound() {
        PaymentRequestDTO paymentRequestDTO = createTestPayment(1L, PaymentType.DEBIT_CARD);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> paymentService.updatePayment(1L, paymentRequestDTO, 1L));

        assertEquals("No payment found", ex.getMessage());

        verify(paymentRepository).findByIdAndOrderUserId(1L, 1L);
    }

    @Test
    void updatePayment_paymentNotInInitiatedStatus() {
        Order order = new Order();
        Payment payment = createTestPayment(order, PaymentType.CREDIT_CARD, PaymentStatus.AUTHORIZED, "1234");
        PaymentRequestDTO paymentRequestDTO = createTestPayment(1L, PaymentType.DEBIT_CARD);
        when(paymentRepository.findByIdAndOrderUserId(1L, 1L)).thenReturn(Optional.of(payment));

        InvalidPaymentStatusException ex = assertThrows(InvalidPaymentStatusException.class, () -> paymentService.updatePayment(1L, paymentRequestDTO, 1L));

        assertEquals("Only INITIATED payments can be updated", ex.getMessage());

        verify(paymentRepository).findByIdAndOrderUserId(1L, 1L);
    }
}
