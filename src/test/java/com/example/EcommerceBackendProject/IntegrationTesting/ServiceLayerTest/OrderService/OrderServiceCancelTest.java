package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.OrderService;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Exception.NoResourceFoundException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.OrderItemTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.OrderTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Repository.OrderRepository;
import com.example.EcommerceBackendProject.Service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderServiceCancelTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void userCancelOrder_success_cancelCreatedOrder() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        assertEquals(10, item.getProduct().getStockQuantity());

        orderService.cancelOrder(createdOrder.getId(), user.getId());

        Order savedOrder = orderRepository.findById(createdOrder.getId()).orElseThrow();

        assertEquals(OrderStatus.CANCELLED, savedOrder.getOrderStatus());
        assertEquals(BigDecimal.valueOf(999.8), savedOrder.getTotalAmount());
        assertEquals(10, item.getProduct().getStockQuantity());
    }

    @Test
    void userCancelOrder_success_cancelPendingPaymentOrder() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.checkout(createdOrder.getId(), user.getId());
        assertEquals(8, item.getProduct().getStockQuantity());

        orderRepository.save(createdOrder);
        orderService.cancelOrder(createdOrder.getId(), user.getId());

        Order savedOrder = orderRepository.findById(createdOrder.getId()).orElseThrow();

        assertEquals(OrderStatus.CANCELLED, savedOrder.getOrderStatus());
        assertEquals(BigDecimal.valueOf(999.8), savedOrder.getTotalAmount());
        assertEquals(10, item.getProduct().getStockQuantity());
    }

    @Test
    void userCancelOrder_failed_cannotCancelPaidOrder() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        createdOrder.markPendingPayment();
        createdOrder.markPaid();
        orderRepository.save(createdOrder);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(createdOrder.getId(), user.getId()));

        assertEquals("Order cannot be canceled", ex.getMessage());
    }

    @Test
    void userCancelOrder_failed_orderNotFound() {
        User user = testDataHelper.createUser();

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> orderService.cancelOrder(999L, user.getId()));

        assertEquals("Order not found", ex.getMessage());
    }

    @Test
    void adminCancelOrder_success() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        assertEquals(10, item.getProduct().getStockQuantity());

        orderService.cancelOrder(createdOrder.getId());

        Order savedOrder = orderRepository.findById(createdOrder.getId()).orElseThrow();

        assertEquals(OrderStatus.CANCELLED, savedOrder.getOrderStatus());
        assertEquals(BigDecimal.valueOf(999.8), savedOrder.getTotalAmount());
        assertEquals(10, item.getProduct().getStockQuantity());
    }

    @Test
    void adminCancelOrder_failed_orderNotFound() {
        User user = testDataHelper.createUser();

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> orderService.cancelOrder(999L));

        assertEquals("No order found", ex.getMessage());
    }

    @Test
    void adminCancelOrder_failed_cannotCancelPaidOrder() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        createdOrder.markPendingPayment();
        createdOrder.markPaid();
        orderRepository.save(createdOrder);
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(createdOrder.getId()));

        assertEquals("Order cannot be canceled", ex.getMessage());
    }
}
