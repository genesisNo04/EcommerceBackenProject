package com.example.EcommerceBackendProject.UnitTest.Utilities;

import com.example.EcommerceBackendProject.DTO.OrderItemRequestDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Entity.OrderItem;
import com.example.EcommerceBackendProject.Entity.Product;

import java.math.BigDecimal;

public class OrderItemsTestUtils {

    public static OrderItem createTestOrderItem(Order order, Product product, int quantity, BigDecimal priceAtPurchase) {
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPriceAtPurchase(priceAtPurchase);
        return item;
    }

    public static OrderItemRequestDTO createOrderItemDto(Long productId, Integer quantity) {
        return new OrderItemRequestDTO(productId, quantity);
    }
}
