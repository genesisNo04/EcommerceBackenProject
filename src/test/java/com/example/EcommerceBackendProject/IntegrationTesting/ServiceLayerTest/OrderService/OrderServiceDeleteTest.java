package com.example.EcommerceBackendProject.IntegrationTesting.ServiceLayerTest.OrderService;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;
import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.ShoppingCartItem;
import com.example.EcommerceBackendProject.Entity.User;
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
public class OrderServiceDeleteTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestDataHelper testDataHelper;

    @Test
    void deleteOrder_success() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());

        orderService.deleteOrder(createdOrder.getId(), user.getId());

        Order searchOrder = orderRepository.findById(createdOrder.getId()).orElse(null);

        assertNull(searchOrder);
    }

    @Test
    void deleteOrder_notFoundOrder() {
        User user = testDataHelper.createUser();

        NoResourceFoundException ex = assertThrows(NoResourceFoundException.class, () ->  orderService.deleteOrder(999L, user.getId()));

        assertEquals("Order not found", ex.getMessage());
    }

    @Test
    void deleteOrder_failed_orderNotCreatedStatus() {
        User user = testDataHelper.createUser();

        ShoppingCartItem item = testDataHelper.createProductAndAddItemToCart("PS5", "Playstation", 10, BigDecimal.valueOf(499.9), 2, user.getId());
        ShoppingCartItem item1 = testDataHelper.createProductAndAddItemToCart("XBOX", "Xbox", 10, BigDecimal.valueOf(499.9), 1, user.getId());

        OrderItemRequestDTO orderItemRequestDTO = OrderItemTestFactory.createOrderItemDto(item.getProduct().getId(), 2);
        OrderItemRequestDTO orderItemRequestDTO1 = OrderItemTestFactory.createOrderItemDto(item1.getProduct().getId(), 1);

        OrderRequestDTO orderRequestDTO = OrderTestFactory.createOrderDTO(List.of(orderItemRequestDTO, orderItemRequestDTO1));

        Order createdOrder = orderService.createOrder(orderRequestDTO, user.getId());
        orderService.cancelOrder(createdOrder.getId(), user.getId());

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> orderService.deleteOrder(createdOrder.getId(), user.getId()));

        assertEquals("Only CREATED orders can be deleted", ex.getMessage());
    }

}
