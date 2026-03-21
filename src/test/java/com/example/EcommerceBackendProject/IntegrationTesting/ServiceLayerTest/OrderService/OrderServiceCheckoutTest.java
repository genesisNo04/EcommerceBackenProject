package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.OrderService;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.OrderItem;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Exception.InvalidOrderItemQuantityException;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.OrderItemTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.OrderTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Repository.OrderRepository;
import com.example.EcommerceBackendProject.Repository.ProductRepository;
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
public class OrderServiceCheckoutTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void userCheckOutOrder_success() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        orderService.checkout(createdOrder.getId(), user.getId());

        Order savedOrder = orderRepository.findById(createdOrder.getId()).orElseThrow();

        assertEquals(OrderStatus.PENDING_PAYMENT, savedOrder.getOrderStatus());
        assertEquals(BigDecimal.valueOf(1499.7), savedOrder.getTotalAmount());

        OrderItem ps5Item = savedOrder.getOrderItems().stream().filter(i -> i.getProduct().getId().equals(item.getProduct().getId())).findFirst().orElseThrow();
        assertEquals(2, ps5Item.getQuantity());

        OrderItem xboxItem = savedOrder.getOrderItems().stream().filter(i -> i.getProduct().getId().equals(item1.getProduct().getId())).findFirst().orElseThrow();
        assertEquals(1, xboxItem.getQuantity());

        assertEquals(savedOrder.getId(), ps5Item.getOrder().getId());
        assertEquals(savedOrder.getId(), xboxItem.getOrder().getId());
        assertEquals(user.getId(), savedOrder.getUser().getId());
        assertEquals(8, item.getProduct().getStockQuantity());
        assertEquals(9, item1.getProduct().getStockQuantity());
    }

    @Test
    void userCheckOutOrder_failed_orderNotInCreatedStatus() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.cancelOrder(createdOrder.getId(), user.getId());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.checkout(createdOrder.getId(), user.getId()));

        assertEquals("Order cannot be checked out", ex.getMessage());
    }

    @Test
    void userCheckOutOrder_success_orderAlreadyCheckout() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.checkout(createdOrder.getId(), user.getId());
        orderService.checkout(createdOrder.getId(), user.getId());

        Order savedOrder = orderRepository.findById(createdOrder.getId()).orElseThrow();
        assertEquals(OrderStatus.PENDING_PAYMENT, savedOrder.getOrderStatus());
        assertEquals(BigDecimal.valueOf(1499.7), savedOrder.getTotalAmount());
        assertEquals(8, item.getProduct().getStockQuantity());
        assertEquals(9, item1.getProduct().getStockQuantity());
    }

    @Test
    void userCheckOutOrder_failed_insufficientStock() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        item.getProduct().setStockQuantity(1);
        productRepository.save(item.getProduct());

        InvalidOrderItemQuantityException ex = assertThrows(InvalidOrderItemQuantityException.class , () -> orderService.checkout(createdOrder.getId(), user.getId()));

        assertEquals("Insufficient stock for product " + item.getProduct().getId(), ex.getMessage());
    }

    @Test
    void adminCheckOutOrder_success() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        orderService.checkout(createdOrder.getId());

        Order savedOrder = orderRepository.findById(createdOrder.getId()).orElseThrow();

        assertEquals(OrderStatus.PENDING_PAYMENT, savedOrder.getOrderStatus());
        assertEquals(BigDecimal.valueOf(1499.7), savedOrder.getTotalAmount());

        OrderItem ps5Item = savedOrder.getOrderItems().stream().filter(i -> i.getProduct().getId().equals(item.getProduct().getId())).findFirst().orElseThrow();
        assertEquals(2, ps5Item.getQuantity());

        OrderItem xboxItem = savedOrder.getOrderItems().stream().filter(i -> i.getProduct().getId().equals(item1.getProduct().getId())).findFirst().orElseThrow();
        assertEquals(1, xboxItem.getQuantity());

        assertEquals(savedOrder.getId(), ps5Item.getOrder().getId());
        assertEquals(savedOrder.getId(), xboxItem.getOrder().getId());
        assertEquals(user.getId(), savedOrder.getUser().getId());
        assertEquals(8, item.getProduct().getStockQuantity());
        assertEquals(9, item1.getProduct().getStockQuantity());
    }

    @Test
    void adminCheckOutOrder_failed_orderNotInCreatedStatus() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.cancelOrder(createdOrder.getId(), user.getId());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.checkout(createdOrder.getId()));

        assertEquals("Order cannot be checked out", ex.getMessage());
    }

    @Test
    void adminCheckOutOrder_success_orderAlreadyCheckout() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.checkout(createdOrder.getId());
        orderService.checkout(createdOrder.getId());

        Order savedOrder = orderRepository.findById(createdOrder.getId()).orElseThrow();
        assertEquals(OrderStatus.PENDING_PAYMENT, savedOrder.getOrderStatus());
        assertEquals(BigDecimal.valueOf(1499.7), savedOrder.getTotalAmount());
        assertEquals(8, item.getProduct().getStockQuantity());
        assertEquals(9, item1.getProduct().getStockQuantity());
    }

    @Test
    void adminCheckOutOrder_failed_insufficientStock() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        item.getProduct().setStockQuantity(1);
        productRepository.save(item.getProduct());

        InvalidOrderItemQuantityException ex = assertThrows(InvalidOrderItemQuantityException.class , () -> orderService.checkout(createdOrder.getId()));

        assertEquals("Insufficient stock for product " + item.getProduct().getId(), ex.getMessage());
    }
}
