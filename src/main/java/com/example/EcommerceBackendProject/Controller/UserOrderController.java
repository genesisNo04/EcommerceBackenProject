package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.OrderRequestDTO;
import com.example.EcommerceBackendProject.DTO.OrderResponseDTO;
import com.example.EcommerceBackendProject.Entity.Order;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Enum.Status;
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
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/v1/users/{userId}/orders")
public class UserOrderController {

    private final OrderService orderService;
    private final PageableSortValidator pageableSortValidator;

    public UserOrderController(OrderService orderService, PageableSortValidator pageableSortValidator) {
        this.orderService = orderService;
        this.pageableSortValidator = pageableSortValidator;
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> findAllOrderByUser(@PathVariable Long userId,
                                                                     @RequestParam(required = false) Status status,
                                                                     @RequestParam(required = false) LocalDate start,
                                                                     @RequestParam(required = false) LocalDate end,
                                                                     @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        pageable = pageableSortValidator.validate(pageable, SortableFields.ORDER.getFields());
        LocalDateTime startTime = (start == null) ? LocalDateTime.of(1970, 1, 1, 0, 0) :  LocalDateTime.of(start, LocalTime.MIDNIGHT);
        LocalDateTime endTime = (end == null) ? LocalDateTime.now() : LocalDateTime.of(end, LocalTime.MIDNIGHT.minusSeconds(1));

        Page<Order> orders = orderService.findUserOrders(userId, status, startTime, endTime, pageable);

        Page<OrderResponseDTO> responseDTOS = orders.map(OrderMapper::toDTO);
        return ResponseEntity.ok(responseDTOS);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> findOrderById(@PathVariable Long userId,
                                                          @PathVariable Long orderId) {

        Order order = orderService.findOrderById(orderId, userId);
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable Long userId,
                                                            @PathVariable Long orderId) {

       orderService.deleteOrder(orderId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@PathVariable Long userId, @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        Order order = orderService.createOrder(orderRequestDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderMapper.toDTO(order));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseDTO> updateOrder(@PathVariable Long userId, @PathVariable Long orderId, @Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        Order order = orderService.updateOrder(orderRequestDTO, orderId, userId);
        return ResponseEntity.ok(OrderMapper.toDTO(order));
    }

}
