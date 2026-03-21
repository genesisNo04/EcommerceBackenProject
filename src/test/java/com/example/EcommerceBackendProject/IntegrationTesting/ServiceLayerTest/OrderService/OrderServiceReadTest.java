package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.OrderService;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.OrderItem;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderServiceReadTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void findOrder_byId() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        Order searchOrder = orderService.findOrderById(createdOrder.getId(), user.getId());

        assertEquals(2, searchOrder.getOrderItems().size());

        OrderItem ps5Item = searchOrder.getOrderItems().stream().filter(i -> i.getProduct().getId().equals(item.getProduct().getId())).findFirst().orElseThrow();
        assertEquals(2, ps5Item.getQuantity());

        OrderItem xboxItem = searchOrder.getOrderItems().stream().filter(i -> i.getProduct().getId().equals(item1.getProduct().getId())).findFirst().orElseThrow();
        assertEquals(1, xboxItem.getQuantity());

        assertEquals(searchOrder.getId(), ps5Item.getOrder().getId());
        assertEquals(searchOrder.getId(), xboxItem.getOrder().getId());
        assertEquals(user.getId(), searchOrder.getUser().getId());
        assertEquals(OrderStatus.CREATED, searchOrder.getOrderStatus());
        assertEquals(BigDecimal.valueOf(1499.7), searchOrder.getTotalAmount());
    }

    @Test
    void findOrder_byId_notFoundOrder() {
        User user = testDataHelper.createUser();

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () ->  orderService.findOrderById(999L, user.getId()));

        assertEquals("Order not found", ex.getMessage());
    }

    @Test
    void findAllOrders() {
        User user = testDataHelper.createUser("testuser", "test123", "test1@gmail.com");
        User user1 = testDataHelper.createUser("testuser1", "test123", "test2@gmail.com");

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));
        OrderRequestDTO orderRequestDTO1 = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Order createdOrder1 = orderService.createOrder(orderRequestDTO1, user1.getId());
        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> orders = orderService.findAllOrders(pageable);

        assertEquals(2, orders.getTotalElements());
        assertEquals(2, orders.getContent().size());
        assertTrue(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).containsAll(Set.of(createdOrder.getId(), createdOrder1.getId())));
    }

    @Test
    void findAllOrders_pagination() {
        User user = testDataHelper.createUser("testuser", "test123", "test1@gmail.com");
        User user1 = testDataHelper.createUser("testuser1", "test123", "test2@gmail.com");
        User user2 = testDataHelper.createUser("testuser2", "test123", "test3@gmail.com");

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Order createdOrder1 = orderService.createOrder(orderRequestDTO, user1.getId());
        Order createdOrder2 = orderService.createOrder(orderRequestDTO, user2.getId());
        Pageable pageable = PageRequest.of(0, 2);

        Page<Order> orders = orderService.findAllOrders(pageable);

        assertEquals(3, orders.getTotalElements());
        assertEquals(2, orders.getContent().size());
        assertTrue(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).containsAll(Set.of(createdOrder.getId(), createdOrder1.getId())));
        assertFalse(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).contains(createdOrder2.getId()));
    }

    @Test
    void findUsersOrders_onlyUserId() {
        User user = testDataHelper.createUser("testuser", "test123", "test1@gmail.com");
        User user1 = testDataHelper.createUser("testuser1", "test123", "test2@gmail.com");
        User user2 = testDataHelper.createUser("testuser2", "test123", "test3@gmail.com");

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Order createdOrder1 = orderService.createOrder(orderRequestDTO, user1.getId());
        Order createdOrder2 = orderService.createOrder(orderRequestDTO, user2.getId());
        Pageable pageable = PageRequest.of(0, 2);

        Page<Order> orders = orderService.findUserOrders(user.getId(), null, null, null, pageable);

        assertEquals(1, orders.getTotalElements());
        assertEquals(1, orders.getContent().size());
        assertTrue(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).contains(createdOrder.getId()));
        assertFalse(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).containsAll(Set.of(createdOrder2.getId(), createdOrder1.getId())));
    }

    @Test
    void findUsersOrders_dateRange() {
        User user = testDataHelper.createUser("testuser", "test123", "test1@gmail.com");
        User user1 = testDataHelper.createUser("testuser1", "test123", "test2@gmail.com");
        User user2 = testDataHelper.createUser("testuser2", "test123", "test3@gmail.com");

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Order createdOrder1 = orderService.createOrder(orderRequestDTO, user1.getId());
        Order createdOrder2 = orderService.createOrder(orderRequestDTO, user2.getId());
        Order createdOrder3 = orderService.createOrder(orderRequestDTO, user.getId());
        Pageable pageable = PageRequest.of(0, 2);

        Page<Order> orders = orderService.findUserOrders(user.getId(), null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), pageable);

        assertEquals(2, orders.getTotalElements());
        assertEquals(2, orders.getContent().size());
        assertTrue(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).containsAll(Set.of(createdOrder.getId(), createdOrder3.getId())));
        assertFalse(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).containsAll(Set.of(createdOrder2.getId(), createdOrder1.getId())));
    }

    @Test
    void findUsersOrders_orderStatus() {
        User user = testDataHelper.createUser("testuser", "test123", "test1@gmail.com");

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.cancelOrder(createdOrder.getId(), user.getId());
        Order createdOrder1 = orderService.createOrder(orderRequestDTO, user.getId());
        Pageable pageable = PageRequest.of(0, 2);

        Page<Order> orders = orderService.findUserOrders(user.getId(), OrderStatus.CREATED, null, null, pageable);

        assertEquals(1, orders.getTotalElements());
        assertEquals(1, orders.getContent().size());
        assertTrue(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).contains(createdOrder1.getId()));
        assertFalse(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).contains(createdOrder.getId()));
    }

    @Test
    void findUsersOrders_allCriteria() {
        User user = testDataHelper.createUser("testuser", "test123", "test1@gmail.com");

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.cancelOrder(createdOrder.getId(), user.getId());
        Order createdOrder1 = orderService.createOrder(orderRequestDTO, user.getId());
        Pageable pageable = PageRequest.of(0, 2);

        Page<Order> orders = orderService.findUserOrders(user.getId(), OrderStatus.CREATED, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), pageable);

        assertEquals(1, orders.getTotalElements());
        assertEquals(1, orders.getContent().size());
        assertTrue(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).contains(createdOrder1.getId()));
        assertFalse(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).contains(createdOrder.getId()));
    }

    @Test
    void findAllFilters_success() {
        User user = testDataHelper.createUser("testuser", "test123", "test1@gmail.com");

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.cancelOrder(createdOrder.getId(), user.getId());
        Order createdOrder1 = orderService.createOrder(orderRequestDTO, user.getId());
        Pageable pageable = PageRequest.of(0, 2);

        Page<Order> orders = orderService.findAllFiltered(user.getId(), OrderStatus.CREATED, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), pageable);

        assertEquals(1, orders.getTotalElements());
        assertEquals(1, orders.getContent().size());
        assertTrue(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).contains(createdOrder1.getId()));
        assertFalse(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).contains(createdOrder.getId()));
    }

    @Test
    void findAllFilters_withDateRange() {
        User user = testDataHelper.createUser("testuser", "test123", "test1@gmail.com");
        User user1 = testDataHelper.createUser("testuser1", "test123", "test2@gmail.com");
        User user2 = testDataHelper.createUser("testuser2", "test123", "test3@gmail.com");

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Order createdOrder1 = orderService.createOrder(orderRequestDTO, user1.getId());
        Order createdOrder2 = orderService.createOrder(orderRequestDTO, user2.getId());
        Order createdOrder3 = orderService.createOrder(orderRequestDTO, user.getId());
        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> orders = orderService.findAllFiltered(null, null, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), pageable);

        assertEquals(4, orders.getTotalElements());
        assertEquals(4, orders.getContent().size());
        assertTrue(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).containsAll(Set.of(createdOrder.getId(), createdOrder1.getId(), createdOrder2.getId(), createdOrder3.getId())));
    }

    @Test
    void findAllFilters_noCriteria() {
        User user = testDataHelper.createUser("testuser", "test123", "test1@gmail.com");
        User user1 = testDataHelper.createUser("testuser1", "test123", "test2@gmail.com");
        User user2 = testDataHelper.createUser("testuser2", "test123", "test3@gmail.com");

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Order createdOrder1 = orderService.createOrder(orderRequestDTO, user1.getId());
        Order createdOrder2 = orderService.createOrder(orderRequestDTO, user2.getId());
        Order createdOrder3 = orderService.createOrder(orderRequestDTO, user.getId());
        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> orders = orderService.findAllFiltered(null, null, null, null, pageable);

        assertEquals(4, orders.getTotalElements());
        assertEquals(4, orders.getContent().size());
        assertTrue(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).containsAll(Set.of(createdOrder.getId(), createdOrder1.getId(), createdOrder2.getId(), createdOrder3.getId())));
    }

    @Test
    void findAllFilters_withAllFilterExceptUserId() {
        User user = testDataHelper.createUser("testuser", "test123", "test1@gmail.com");
        User user1 = testDataHelper.createUser("testuser1", "test123", "test2@gmail.com");
        User user2 = testDataHelper.createUser("testuser2", "test123", "test3@gmail.com");

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Order createdOrder1 = orderService.createOrder(orderRequestDTO, user1.getId());
        Order createdOrder2 = orderService.createOrder(orderRequestDTO, user2.getId());
        Order createdOrder3 = orderService.createOrder(orderRequestDTO, user.getId());
        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> orders = orderService.findAllFiltered(null, OrderStatus.CREATED, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), pageable);

        assertEquals(4, orders.getTotalElements());
        assertEquals(4, orders.getContent().size());
        assertTrue(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).containsAll(Set.of(createdOrder.getId(), createdOrder1.getId(), createdOrder2.getId(), createdOrder3.getId())));
    }

    @Test
    void findAllFilters_withOrderStatus() {
        User user = testDataHelper.createUser("testuser", "test123", "test1@gmail.com");
        User user1 = testDataHelper.createUser("testuser1", "test123", "test2@gmail.com");
        User user2 = testDataHelper.createUser("testuser2", "test123", "test3@gmail.com");

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.cancelOrder(createdOrder.getId(), user.getId());
        Order createdOrder1 = orderService.createOrder(orderRequestDTO, user1.getId());
        Order createdOrder2 = orderService.createOrder(orderRequestDTO, user2.getId());
        Order createdOrder3 = orderService.createOrder(orderRequestDTO, user.getId());
        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> orders = orderService.findAllFiltered(null, OrderStatus.CREATED, null, null, pageable);

        assertEquals(3, orders.getTotalElements());
        assertEquals(3, orders.getContent().size());
        assertTrue(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).containsAll(Set.of(createdOrder1.getId(), createdOrder2.getId(), createdOrder3.getId())));
    }

    @Test
    void findAllFilters_withUserId() {
        User user = testDataHelper.createUser("testuser", "test123", "test1@gmail.com");
        User user1 = testDataHelper.createUser("testuser1", "test123", "test2@gmail.com");
        User user2 = testDataHelper.createUser("testuser2", "test123", "test3@gmail.com");

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Order createdOrder1 = orderService.createOrder(orderRequestDTO, user1.getId());
        Order createdOrder2 = orderService.createOrder(orderRequestDTO, user2.getId());
        Order createdOrder3 = orderService.createOrder(orderRequestDTO, user.getId());
        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> orders = orderService.findAllFiltered(user.getId(), null, null, null, pageable);

        assertEquals(2, orders.getTotalElements());
        assertEquals(2, orders.getContent().size());
        assertTrue(orders.getContent().stream().map(Order::getId).collect(Collectors.toSet()).containsAll(Set.of(createdOrder.getId(), createdOrder3.getId())));
    }

    @Test
    void adminFindOrder_byId() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        Pageable pageable = PageRequest.of(0, 10);

        Page<Order> searchOrder = orderService.findByOrderId(createdOrder.getId(), pageable);

        assertEquals(1, searchOrder.getContent().size());
        assertEquals(1, searchOrder.getTotalElements());
    }

    @Test
    void adminFindOrder_byId_notFound() {
        Pageable pageable = PageRequest.of(0, 10);

        NoResourceFoundException ex =  assertThrows(NoResourceFoundException.class, () -> orderService.findByOrderId(999L, pageable));

        assertEquals("No order found", ex.getMessage());
    }
}
