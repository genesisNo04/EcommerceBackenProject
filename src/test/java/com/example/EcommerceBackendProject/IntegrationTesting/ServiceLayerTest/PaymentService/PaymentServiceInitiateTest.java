package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.PaymentService;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Entity.Payment.PaymentGateway;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
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
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PaymentServiceInitiateTest {

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
    void initiatePayment_success() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        Payment createdPayment = paymentService.initiatePayment(createdOrder.getId(), user.getId(), PaymentType.CREDIT_CARD);

        Payment savedPayment = paymentRepository.findById(createdPayment.getId()).orElseThrow();

        assertEquals(PaymentStatus.INITIATED, savedPayment.getStatus());
        assertEquals(BigDecimal.valueOf(999.8), savedPayment.getAmount());
        assertEquals(createdOrder.getId(), savedPayment.getOrder().getId());
        assertEquals(savedPayment.getId(), createdOrder.getPayment().getId());
    }

    @Test
    void initiatePayment_failed_orderNotFound() {
        User user = testDataHelper.createUser();

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> paymentService.initiatePayment(999L, user.getId(), PaymentType.CREDIT_CARD));

        assertEquals("No order found", ex.getMessage());
    }

    @Test
    void initiatePayment_failed_paymentAlreadyExist() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        Payment createdPayment = paymentService.initiatePayment(createdOrder.getId(), user.getId(), PaymentType.CREDIT_CARD);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> paymentService.initiatePayment(createdOrder.getId(), user.getId(), PaymentType.CREDIT_CARD));

        assertEquals("Payment already exist", ex.getMessage());
    }
}
