package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.PaymentService;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.DTO.PaymentRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Entity.Payment.PaymentGateway;
import com.example.EcommerceBackendProject.Entity.Payment.PaymentResult;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Exception.InvalidPaymentStatusException;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.OrderItemTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.OrderTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Repository.PaymentRepository;
import com.example.EcommerceBackendProject.Service.OrderService;
import com.example.EcommerceBackendProject.Service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.example.EcommerceBackendProject.IntegrationTesting.Utilities.PaymentTestFactory.createPaymentDTO;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PaymentServiceUpdateTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TestDataHelper testDataHelper;

    @MockitoBean
    private PaymentGateway paymentGateway;

    @Test
    void updatePayment_success() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.checkout(createdOrder.getId(), user.getId());

        when(paymentGateway.charge(any())).thenReturn(PaymentResult.success("TEST-123"));

        Payment createdPayment = paymentService.initiatePayment(createdOrder.getId(), user.getId(), PaymentType.CREDIT_CARD);
        PaymentRequestDTO paymentRequestDTO = createPaymentDTO(createdOrder.getId(), PaymentType.DEBIT_CARD);
        paymentService.updatePayment(createdPayment.getId(), paymentRequestDTO, user.getId());

        Payment savedPayment = paymentRepository.findById(createdPayment.getId()).orElse(null);
        Order order = orderService.findOrderById(createdOrder.getId(), user.getId());

        assertEquals(PaymentType.DEBIT_CARD, savedPayment.getPaymentType());
        assertEquals(order.getId(), savedPayment.getOrder().getId());
        assertEquals(savedPayment.getId(), order.getPayment().getId());
    }

    @Test
    void updatePayment_failed_paymentNotInitiated() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.checkout(createdOrder.getId(), user.getId());

        when(paymentGateway.charge(any())).thenReturn(PaymentResult.success("TEST-123"));

        Payment createdPayment = paymentService.initiatePayment(createdOrder.getId(), user.getId(), PaymentType.CREDIT_CARD);
        PaymentRequestDTO paymentRequestDTO = createPaymentDTO(createdOrder.getId(), PaymentType.DEBIT_CARD);
        paymentService.processPayment(createdOrder.getId(), user.getId());

        InvalidPaymentStatusException ex = assertThrows(InvalidPaymentStatusException.class, () -> paymentService.updatePayment(createdPayment.getId(), paymentRequestDTO, user.getId()));

        assertEquals("Only INITIATED payments can be updated", ex.getMessage());
    }

    @Test
    void updatePayment_failed_noPaymentFound() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.checkout(createdOrder.getId(), user.getId());

        when(paymentGateway.charge(any())).thenReturn(PaymentResult.success("TEST-123"));

        PaymentRequestDTO paymentRequestDTO = createPaymentDTO(createdOrder.getId(), PaymentType.DEBIT_CARD);

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> paymentService.updatePayment(999L, paymentRequestDTO, user.getId()));

        assertEquals("No payment found", ex.getMessage());
    }
}
