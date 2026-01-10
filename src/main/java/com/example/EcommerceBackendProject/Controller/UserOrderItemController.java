package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.OrderItemResponseDTO;
import com.example.EcommerceBackendProject.Entity.OrderItem;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Mapper.OrderItemMapper;
import com.example.EcommerceBackendProject.Service.OrderItemService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/{userId}/orders/{orderId}/items")
public class UserOrderItemController {

    private final OrderItemService orderItemService;
    private final PageableSortValidator pageableSortValidator;

    public UserOrderItemController(OrderItemService orderItemService, PageableSortValidator pageableSortValidator) {
        this.orderItemService = orderItemService;
        this.pageableSortValidator = pageableSortValidator;
    }

    @GetMapping
    public ResponseEntity<Page<OrderItemResponseDTO>> getOrderItems(@PathVariable Long userId, @PathVariable Long orderId,
                                                                   @PageableDefault(size = 10) Pageable pageable) {

        pageable = pageableSortValidator.validate(pageable, SortableFields.ORDERITEM.getFields());
        Page<OrderItem> orderItems = orderItemService.findOrderItemsForUserInOrder(userId, orderId, pageable);
        Page<OrderItemResponseDTO> responseDTOS = orderItems.map(OrderItemMapper::toDTO);
        return ResponseEntity.ok(responseDTOS);
    }

    @PostMapping("/{productId}/{quantity}")
    public ResponseEntity<OrderItemResponseDTO> addOrderItemToOrder(@PathVariable Long userId,
                                                                    @PathVariable Long orderId,
                                                                    @PathVariable Long productId,
                                                                    @PathVariable int quantity) {

        OrderItem orderItem = orderItemService.addItemToOrder(orderId, productId, quantity, userId);
        return ResponseEntity.ok(OrderItemMapper.toDTO(orderItem));
    }

    @PatchMapping("/{itemId}/{quantity}")
    public ResponseEntity<OrderItemResponseDTO> updateOrderItemQuantity(@PathVariable Long userId,
                                                                    @PathVariable Long orderId,
                                                                    @PathVariable Long itemId,
                                                                    @PathVariable int quantity) {

        OrderItem orderItem = orderItemService.updateOrderItemQuantity(itemId, orderId, quantity, userId);
        return ResponseEntity.ok(OrderItemMapper.toDTO(orderItem));
    }
}
