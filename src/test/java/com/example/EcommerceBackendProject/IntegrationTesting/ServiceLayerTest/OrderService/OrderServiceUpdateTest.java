package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.OrderService;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.OrderItem;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Exception.InvalidOrderItemQuantityException;
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
public class OrderServiceUpdateTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void updateOrder_success() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        Order savedOrder = orderRepository.findById(createdOrder.getId()).orElseThrow();

        assertEquals(1, savedOrder.getOrderItems().size());
        OrderItem ps5Item = savedOrder.getOrderItems().stream().filter(i -> item.getProduct().getId().equals(i.getProduct().getId())).findFirst().orElse(null);
        assertNotNull(ps5Item);

        OrderRequestDTO orderUpdateRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO1));

        Order updatedOrder = orderService.updateOrder(orderUpdateRequestDTO, createdOrder.getId(), user.getId());

        savedOrder = orderRepository.findById(updatedOrder.getId()).orElseThrow();

        assertEquals(1, savedOrder.getOrderItems().size());
        OrderItem xboxItem = savedOrder.getOrderItems().stream().filter(i -> item1.getProduct().getId().equals(i.getProduct().getId())).findFirst().orElse(null);
        assertEquals(1, xboxItem.getQuantity());

        ps5Item = savedOrder.getOrderItems().stream().filter(i -> item.getProduct().getId().equals(i.getProduct().getId())).findFirst().orElse(null);
        assertNull(ps5Item);

        assertEquals(savedOrder.getId(), xboxItem.getOrder().getId());
        assertEquals(user.getId(), savedOrder.getUser().getId());
        assertEquals(OrderStatus.CREATED, savedOrder.getOrderStatus());
        assertEquals(BigDecimal.valueOf(499.9), savedOrder.getTotalAmount());
    }

    @Test
    void updateOrder_success_updateSameValue() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        OrderRequestDTO orderUpdateRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order updatedOrder = orderService.updateOrder(orderUpdateRequestDTO, createdOrder.getId(), user.getId());

        Order savedOrder = orderRepository.findById(updatedOrder.getId()).orElseThrow();

        assertEquals(1, savedOrder.getOrderItems().size());
    }

    @Test
    void updateOrder_failed_orderNotFound() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderUpdateRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> orderService.updateOrder(orderUpdateRequestDTO, 999L, user.getId()));

        assertEquals("Order not found",ex.getMessage());
    }

    @Test
    void updateOrder_failed_orderNotInCreated() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        orderService.cancelOrder(createdOrder.getId(), user.getId());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.updateOrder(orderRequestDTO, createdOrder.getId(), user.getId()));

        assertEquals("Only CREATED orders can be updated", ex.getMessage());
    }

    @Test
    void updateOrder_failed_noProductFound() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(999L, 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        OrderRequestDTO orderUpdateRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO1));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> orderService.updateOrder(orderUpdateRequestDTO, createdOrder.getId(), user.getId()));

        assertEquals("No product found",ex.getMessage());
    }

    @Test
    void updateOrder_failed_invalidQuantity() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), -1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        OrderRequestDTO orderUpdateRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO1));

        InvalidOrderItemQuantityException ex = assertThrows(InvalidOrderItemQuantityException.class, () -> orderService.updateOrder(orderUpdateRequestDTO, createdOrder.getId(), user.getId()));

        assertEquals("Invalid quantity",ex.getMessage());
    }

    @Test
    void updateOrder_failed_insufficientStock() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 11);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        OrderRequestDTO orderUpdateRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO1));

        InvalidOrderItemQuantityException ex = assertThrows(InvalidOrderItemQuantityException.class, () -> orderService.updateOrder(orderUpdateRequestDTO, createdOrder.getId(), user.getId()));

        assertEquals("Insufficient stock",ex.getMessage());
    }
}
