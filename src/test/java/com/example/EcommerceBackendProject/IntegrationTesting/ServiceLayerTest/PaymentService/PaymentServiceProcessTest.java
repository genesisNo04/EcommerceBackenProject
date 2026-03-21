package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.PaymentService;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.OrderItemTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.OrderTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Repository.PaymentRepository;
import com.example.EcommerceBackendProject.Service.OrderService;
import com.example.EcommerceBackendProject.Service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Import(PaymentServiceSuccessTestConfig.class)
public class PaymentServiceProcessTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TestDataHelper testDataHelper;

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

        Payment createdPayment = paymentService.processPayment(createdOrder.getId(), user.getId(), PaymentType.CREDIT_CARD);

        Payment savedPayment = paymentRepository.findById(createdPayment.getId()).orElseThrow();
        Order order = orderService.findOrderById(createdOrder.getId(), user.getId());

        assertEquals(OrderStatus.PAID, order.getOrderStatus());
    }
}
