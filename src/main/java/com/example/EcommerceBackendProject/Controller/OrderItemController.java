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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders/{orderId}/items")

public class OrderItemController {
    private final OrderItemService orderItemService;
    private final PageableSortValidator pageableSortValidator;

    public OrderItemController(OrderItemService orderItemService, PageableSortValidator pageableSortValidator) {
        this.orderItemService = orderItemService;
        this.pageableSortValidator = pageableSortValidator;
    }

    @GetMapping
    public ResponseEntity<Page<OrderItemResponseDTO>> getOrderItems(@PathVariable Long orderId,
                                                                    @PageableDefault(size = 10) Pageable pageable) {

        pageable = pageableSortValidator.validate(pageable, SortableFields.ORDERITEM.getFields());
        Page<OrderItem> orderItems = orderItemService.findOrderItems(orderId, pageable);
        Page<OrderItemResponseDTO> responseDTOS = orderItems.map(OrderItemMapper::toDTO);
        return ResponseEntity.ok(responseDTOS);
    }
}
