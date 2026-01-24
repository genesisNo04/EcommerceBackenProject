package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.OrderResponseDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Mapper.OrderMapper;
import com.example.EcommerceBackendProject.Service.OrderService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
@PreAuthorize("hasRole('ADMIN')")
public class OrderController {

    private final OrderService orderService;
    private final PageableSortValidator pageableSortValidator;

    public OrderController(OrderService orderService, PageableSortValidator pageableSortValidator) {
        this.orderService = orderService;
        this.pageableSortValidator = pageableSortValidator;
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> findAllOrder(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        pageable = pageableSortValidator.validate(pageable, SortableFields.ORDER.getFields());
        Page<Order> orders = orderService.findAllOrders(pageable);
        Page<OrderResponseDTO> responseDTOS = orders.map(OrderMapper::toDTO);
        return ResponseEntity.ok(responseDTOS);
    }
}
