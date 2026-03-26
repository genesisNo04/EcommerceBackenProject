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
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.*;
import com.example.EcommerceBackendProject.Repository.OrderRepository;
import com.example.EcommerceBackendProject.Service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderServiceCreateTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void createOrder_success() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        Order savedOrder = orderRepository.findById(createdOrder.getId()).orElseThrow();

        assertEquals(2, savedOrder.getOrderItems().size());

        OrderItem ps5Item = savedOrder.getOrderItems().stream().filter(i -> i.getProduct().getId().equals(item.getProduct().getId())).findFirst().orElseThrow();
        assertEquals(2, ps5Item.getQuantity());

        OrderItem xboxItem = savedOrder.getOrderItems().stream().filter(i -> i.getProduct().getId().equals(item1.getProduct().getId())).findFirst().orElseThrow();
        assertEquals(1, xboxItem.getQuantity());

        assertEquals(savedOrder.getId(), ps5Item.getOrder().getId());
        assertEquals(savedOrder.getId(), xboxItem.getOrder().getId());
        assertEquals(user.getId(), savedOrder.getUser().getId());
        assertEquals(OrderStatus.CREATED, savedOrder.getOrderStatus());
        assertEquals(BigDecimal.valueOf(1499.7), savedOrder.getTotalAmount());
        assertEquals(10, item.getProduct().getStockQuantity());
        assertEquals(10, item1.getProduct().getStockQuantity());
    }

    @Test
    void createOrder_success_orderMoreThanQuantityInCart() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 3);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        Order savedOrder = orderRepository.findById(createdOrder.getId()).orElseThrow();

        assertEquals(2, savedOrder.getOrderItems().size());
        assertEquals(user.getId(), savedOrder.getUser().getId());
        assertEquals(OrderStatus.CREATED, savedOrder.getOrderStatus());
        assertEquals(BigDecimal.valueOf(2499.5), savedOrder.getTotalAmount());
    }

    @Test
    void createOrder_failed_userNotFound() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> orderService.createOrder(orderRequestDTO, 999L));

        assertEquals("No user found", ex.getMessage());
    }

    @Test
    void createOrder_failed_productNotFound() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(999L, 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO1));

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () -> orderService.createOrder(orderRequestDTO, user.getId()));

        assertEquals("No product found", ex.getMessage());
    }

    @Test
    void createOrder_failed_invalidQuantity() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), -1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO1));

        InvalidOrderItemQuantityException ex = assertThrows(InvalidOrderItemQuantityException.class, () -> orderService.createOrder(orderRequestDTO, user.getId()));

        assertEquals("Invalid quantity", ex.getMessage());
    }

    @Test
    void createOrder_failed_insufficientStock() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 11);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO1));

        InvalidOrderItemQuantityException ex = assertThrows(InvalidOrderItemQuantityException.class, () -> orderService.createOrder(orderRequestDTO, user.getId()));

        assertEquals("Insufficient stock", ex.getMessage());
    }
}
