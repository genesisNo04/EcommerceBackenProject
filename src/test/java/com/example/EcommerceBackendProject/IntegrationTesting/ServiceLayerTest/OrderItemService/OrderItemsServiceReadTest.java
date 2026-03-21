package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.OrderItemService;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.OrderItem;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.OrderItemTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.OrderTestFactory;
import com.example.EcommerceBackendProject.IntegrationTesting.Utilities.TestDataHelper;
import com.example.EcommerceBackendProject.Service.OrderItemService;
import com.example.EcommerceBackendProject.Service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderItemsServiceReadTest {

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void findOrderItems_byOrder() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Set<Long> ids = createdOrder.getOrderItems().stream().map(OrderItem::getId).collect(Collectors.toSet());
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderItem> items = orderItemService.findOrderItems(createdOrder.getId(), pageable);

        assertEquals(2, items.getTotalElements());
        assertEquals(2, items.getContent().size());
        assertTrue(items.getContent().stream().map(OrderItem::getId).collect(Collectors.toSet()).containsAll(ids));
    }

    @Test
    void findOrderItems_byOrder_pagination() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Set<Long> ids = createdOrder.getOrderItems().stream().map(OrderItem::getId).collect(Collectors.toSet());
        Pageable pageable = PageRequest.of(0, 1);

        Page<OrderItem> items = orderItemService.findOrderItems(createdOrder.getId(), pageable);

        assertEquals(2, items.getTotalElements());
        assertEquals(1, items.getContent().size());
    }

    @Test
    void findOrderItems_noResults() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderItem> items = orderItemService.findOrderItems(999L, pageable);

        assertEquals(0, items.getTotalElements());
        assertTrue(items.getContent().isEmpty());
    }

    @Test
    void findOrderItems_byUser() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Set<Long> ids = createdOrder.getOrderItems().stream().map(OrderItem::getId).collect(Collectors.toSet());
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderItem> items = orderItemService.findAllOrderItemsForUser(user.getId(), pageable);

        assertEquals(2, items.getTotalElements());
        assertEquals(2, items.getContent().size());
        assertTrue(items.getContent().stream().map(OrderItem::getId).collect(Collectors.toSet()).containsAll(ids));
    }

    @Test
    void findOrderItems_byUser_pagination() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Set<Long> ids = createdOrder.getOrderItems().stream().map(OrderItem::getId).collect(Collectors.toSet());
        Pageable pageable = PageRequest.of(0, 1);

        Page<OrderItem> items = orderItemService.findAllOrderItemsForUser(user.getId(), pageable);

        assertEquals(2, items.getTotalElements());
        assertEquals(1, items.getContent().size());
    }

    @Test
    void findOrderItems_byUser_noResults() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderItem> items = orderItemService.findAllOrderItemsForUser(999L, pageable);

        assertEquals(0, items.getTotalElements());
        assertTrue(items.getContent().isEmpty());
    }

    @Test
    void findOrderItems_byUserAndOrder() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Set<Long> ids = createdOrder.getOrderItems().stream().map(OrderItem::getId).collect(Collectors.toSet());
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderItem> items = orderItemService.findOrderItemsForUserInOrder(user.getId(), createdOrder.getId(), pageable);

        assertEquals(2, items.getTotalElements());
        assertEquals(2, items.getContent().size());
        assertTrue(items.getContent().stream().map(OrderItem::getId).collect(Collectors.toSet()).containsAll(ids));
    }

    @Test
    void findOrderItems_byUserAndOrder_pagination() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Set<Long> ids = createdOrder.getOrderItems().stream().map(OrderItem::getId).collect(Collectors.toSet());
        Pageable pageable = PageRequest.of(0, 1);

        Page<OrderItem> items = orderItemService.findOrderItemsForUserInOrder(user.getId(), createdOrder.getId(), pageable);

        assertEquals(2, items.getTotalElements());
        assertEquals(1, items.getContent().size());
    }

    @Test
    void findOrderItems_byUserAndOrder_noResults() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderItem> items = orderItemService.findOrderItemsForUserInOrder(999L, 999L, pageable);

        assertEquals(0, items.getTotalElements());
        assertTrue(items.getContent().isEmpty());
    }

    @Test
    void findOrderItemsForUserInOrder_wrongUser_shouldReturnEmpty() {
        User user1 = testDataHelper.createUser("test1", "test123", "test1@gmail.com");
        User user2 = testDataHelper.createUser("test2", "test123", "test2@gmail.com");

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart(
                "PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 1, user1.getId()
        );

        OrderItemRequestDTO dto = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 1);
        OrderRequestDTO orderDTO = OrderTestFactory.createOrderDTO(List.of(dto));

        Order order = orderService.createOrder(orderDTO, user1.getId());

        Pageable pageable = PageRequest.of(0, 10);

        Page<OrderItem> items = orderItemService
                .findOrderItemsForUserInOrder(user2.getId(), order.getId(), pageable);

        assertTrue(items.isEmpty());
    }
}
