package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.PaymentResponseDTO;
import com.example.EcommerceBackendProject.Entity.Payment;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Mapper.PaymentMapper;
import com.example.EcommerceBackendProject.Service.PaymentService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/users/{userId}/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PageableSortValidator pageableSortValidator;

    public PaymentController(PaymentService paymentService, PageableSortValidator pageableSortValidator) {
        this.paymentService = paymentService;
        this.pageableSortValidator = pageableSortValidator;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponseDTO> getPaymentForOrder(@PathVariable Long userId, @PathVariable Long orderId) {
        Payment payment = paymentService.findPaymentByOrderIdAndUserId(orderId, userId);
        return ResponseEntity.ok(PaymentMapper.toDTO(payment));
    }

    @GetMapping("/{status}")
    public ResponseEntity<Page<PaymentResponseDTO>> getPaymentByStatus(@PathVariable Long userId,
                                                                 @PathVariable PaymentStatus status,
                                                                 @PageableDefault(size = 10) Pageable pageable) {
        pageable = pageableSortValidator.validate(pageable, SortableFields.ORDER.getFields());
        Page<Payment> payment = paymentService.findPaymentByStatusAndUserId(status, userId, pageable);
        return ResponseEntity.ok(payment.map(PaymentMapper::toDTO));
    }

    @GetMapping
    public ResponseEntity<Page<PaymentResponseDTO>> getPaymentByStatus(@PathVariable Long userId,
                                                                       @RequestParam(required = false) PaymentStatus status,
                                                                       @RequestParam(required = false) PaymentType type,
                                                                       @RequestParam(required = false) LocalDate start,
                                                                       @RequestParam(required = false) LocalDate end,
                                                                       @PageableDefault(size = 10) Pageable pageable) {
        pageable = pageableSortValidator.validate(pageable, SortableFields.ORDER.getFields());
        Page<Payment> payment;
        if (status != null && type != null) {
            payment = paymentService.findPaymentByPaymentTypeAndStatusAndUserId(type, status, userId, pageable);
        } else if (status != null) {
            payment = paymentService.findPaymentByStatusAndUserId(status, userId, pageable);
        } else if (type != null) {
            payment = paymentService.findPaymentByPaymentTypeAndUserId(type, userId, pageable);
        } else {
            payment = paymentService.findPaymentByUserId(userId, pageable);
        }

        return ResponseEntity.ok(payment.map(PaymentMapper::toDTO));
    }


}
