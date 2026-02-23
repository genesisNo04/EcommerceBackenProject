package com.example.EcommerceBackendProject.UnitTest.Utilities;

import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.OrderItem;
import com.example.EcommerceBackendProject.Entity.Payment.Payment;
import com.example.EcommerceBackendProject.Entity.User;
import com.example.EcommerceBackendProject.Enum.OrderStatus;

import java.math.BigDecimal;
import java.util.Set;

public class OrderTestUtils {

    public static Order createTestOrder(User user, BigDecimal totalAmount, OrderStatus orderStatus, Set<OrderItem> orderItems, Payment payment) {
        Order order = new Order();
        order.setUser(user);
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(orderStatus);
        order.setOrderItems(orderItems);
        order.setPayment(payment);
        return order;
    }
}
