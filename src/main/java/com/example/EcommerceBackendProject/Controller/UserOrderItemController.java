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
@RequestMapping("/v1/users/{userId}")
public class UserOrderItemController {

    private final OrderItemService orderItemService;
    private final PageableSortValidator pageableSortValidator;

    public UserOrderItemController(OrderItemService orderItemService, PageableSortValidator pageableSortValidator) {
        this.orderItemService = orderItemService;
        this.pageableSortValidator = pageableSortValidator;
    }

    @GetMapping("/orders/{orderId}/items")
    public ResponseEntity<Page<OrderItemResponseDTO>> getOrderItems(@PathVariable Long userId, @PathVariable Long orderId,
                                                                   @PageableDefault(size = 10) Pageable pageable) {

        pageable = pageableSortValidator.validate(pageable, SortableFields.ORDERITEM.getFields());
        Page<OrderItem> orderItems = orderItemService.findOrderItemsForUserInOrder(userId, orderId, pageable);
        Page<OrderItemResponseDTO> responseDTOS = orderItems.map(OrderItemMapper::toDTO);
        return ResponseEntity.ok(responseDTOS);
    }

    @GetMapping
    public ResponseEntity<Page<OrderItemResponseDTO>> getAllOrderItems(@PathVariable Long userId,
                                                                    @PageableDefault(size = 10) Pageable pageable) {

        pageable = pageableSortValidator.validate(pageable, SortableFields.ORDERITEM.getFields());
        Page<OrderItem> orderItems = orderItemService.findAllOrderItemsForUser(userId, pageable);
        Page<OrderItemResponseDTO> responseDTOS = orderItems.map(OrderItemMapper::toDTO);
        return ResponseEntity.ok(responseDTOS);
    }
}
