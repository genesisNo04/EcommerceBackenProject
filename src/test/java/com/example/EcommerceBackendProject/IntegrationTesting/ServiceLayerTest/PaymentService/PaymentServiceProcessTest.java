package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.PaymentService;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Entity.Payment.PaymentGateway;
import com.example.EcommerceBackendProject.Entity.Payment.PaymentResult;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PaymentServiceProcessTest {

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
    void processPayment_success() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.checkout(createdOrder.getId(), user.getId());

        when(paymentGateway.charge(any())).thenReturn(PaymentResult.success("TEST-123"));

        Payment createdPayment = paymentService.initiatePayment(createdOrder.getId(), user.getId(), PaymentType.CREDIT_CARD);
        paymentService.processPayment(createdOrder.getId(), user.getId());

        Payment savedPayment = paymentRepository.findById(createdPayment.getId()).orElseThrow();
        Order order = orderService.findOrderById(createdOrder.getId(), user.getId());

        assertEquals(PaymentType.CREDIT_CARD, savedPayment.getPaymentType());
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        assertEquals(savedPayment.getId(), order.getPayment().getId());
    }

    @Test
    void processPayment_failed() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.checkout(createdOrder.getId(), user.getId());

        when(paymentGateway.charge(any())).thenReturn(PaymentResult.failed());

        Payment createdPayment = paymentService.initiatePayment(createdOrder.getId(), user.getId(), PaymentType.CREDIT_CARD);
        paymentService.processPayment(createdOrder.getId(), user.getId());

        Payment savedPayment = paymentRepository.findById(createdPayment.getId()).orElseThrow();
        Order order = orderService.findOrderById(createdOrder.getId(), user.getId());

        assertEquals(OrderStatus.FAILED, order.getOrderStatus());
        assertEquals(savedPayment.getId(), order.getPayment().getId());
    }

    @Test
    void processPayment_failed_orderNotFound() {
        User user = testDataHelper.createUser();

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> paymentService.processPayment(999L, user.getId()));

        assertEquals("No order found", ex.getMessage());
    }

    @Test
    void processPayment_failed_notPayable() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.cancelOrder(createdOrder.getId(), user.getId());

        when(paymentGateway.charge(any())).thenReturn(PaymentResult.success("TEST-123"));

        paymentService.initiatePayment(createdOrder.getId(), user.getId(), PaymentType.CREDIT_CARD);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> paymentService.processPayment(createdOrder.getId(), user.getId()));

        assertEquals("Order is not payable", ex.getMessage());
    }

    @Test
    void processPayment_success_processPaymentSecondTime() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.checkout(createdOrder.getId(), user.getId());

        when(paymentGateway.charge(any())).thenReturn(PaymentResult.success("TEST-123"));

        Payment createdPayment = paymentService.initiatePayment(createdOrder.getId(), user.getId(), PaymentType.CREDIT_CARD);
        paymentService.processPayment(createdOrder.getId(), user.getId());
        paymentService.processPayment(createdOrder.getId(), user.getId());

        Payment savedPayment = paymentRepository.findById(createdPayment.getId()).orElseThrow();
        Order order = orderService.findOrderById(createdOrder.getId(), user.getId());

        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        assertEquals(PaymentType.CREDIT_CARD, savedPayment.getPaymentType());
        assertEquals(savedPayment.getId(), order.getPayment().getId());
    }
}
