package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.DTO.OrderResponseDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Enum.OrderStatus;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Mapper.OrderMapper;
import com.example.EcommerceBackendProject.Service.OrderService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    public ResponseEntity<Page<OrderResponseDTO>> findAllOrder(@RequestParam(required = false) Long userId,
                                                               @RequestParam(required = false) Long orderId,
                                                               @RequestParam(required = false) OrderStatus orderStatus,
                                                               @RequestParam(required = false) LocalDate start,
                                                               @RequestParam(required = false) LocalDate end,
                                                               @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        pageable = pageableSortValidator.validate(pageable, SortableFields.ORDER.getFields());

        LocalDateTime startTime = (start == null) ? LocalDateTime.of(1970, 1, 1, 0, 0) :  LocalDateTime.of(start, LocalTime.MIDNIGHT);
        LocalDateTime endTime = (end == null) ? LocalDateTime.now() : LocalDateTime.of(end, LocalTime.MIDNIGHT.minusSeconds(1));

        Page<Order> orders = orderService.findAllFiltered(userId, orderStatus, startTime, endTime, pageable);

        Page<OrderResponseDTO> responseDTOS = orders.map(OrderMapper::toDTO);
        return ResponseEntity.ok(responseDTOS);
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@RequestParam Long userId, @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        Order order = orderService.createOrder(orderRequestDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderMapper.toDTO(order));
    }

    @PostMapping("/checkout/{orderId}")
    public ResponseEntity<OrderResponseDTO> checkoutOrder(@PathVariable Long orderId) {
        Order order = orderService.checkout(orderId);
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> updateOrder(@RequestParam Long userId, @PathVariable Long orderId, @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        Order order = orderService.updateOrder(orderRequestDTO, orderId, userId);
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }
}
