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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

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
}
