package com.example.EcommerceBackendProject.Controller;

import com.example.EcommerceBackendProject.DTO.PaymentRequestDTO;
import com.example.EcommerceBackendProject.DTO.PaymentResponseDTO;
import com.example.EcommerceBackendProject.Entity.Payment;
import com.example.EcommerceBackendProject.Enum.PaymentStatus;
import com.example.EcommerceBackendProject.Enum.PaymentType;
import com.example.EcommerceBackendProject.Enum.SortableFields;
import com.example.EcommerceBackendProject.Mapper.PaymentMapper;
import com.example.EcommerceBackendProject.Service.PaymentService;
import com.example.EcommerceBackendProject.Utilities.PageableSortValidator;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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

    @GetMapping
    public ResponseEntity<Page<PaymentResponseDTO>> getPaymentByStatus(@PathVariable Long userId,
                                                                       @RequestParam(required = false) PaymentStatus status,
                                                                       @RequestParam(required = false) PaymentType type,
                                                                       @RequestParam(required = false) LocalDate start,
                                                                       @RequestParam(required = false) LocalDate end,
                                                                       @PageableDefault(size = 10) Pageable pageable) {


        LocalDateTime startTime = (start == null) ? null : start.atStartOfDay();
        LocalDateTime endTime = (end == null) ? null : end.plusDays(1).atStartOfDay();

        pageable = pageableSortValidator.validate(pageable, SortableFields.PAYMENT.getFields());
        Page<Payment> payment = paymentService.findPayments(userId, status, type, startTime, endTime, pageable);

        return ResponseEntity.ok(payment.map(PaymentMapper::toDTO));
    }

    @PostMapping
    public ResponseEntity<PaymentResponseDTO> createPayment(@PathVariable Long userId, @Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
        Payment payment = paymentService.createPayment(paymentRequestDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(PaymentMapper.toDTO(payment));
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long userId, @PathVariable Long paymentId) {
        paymentService.deletePayment(paymentId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<PaymentResponseDTO> updatePayment(@PathVariable Long userId, @PathVariable Long paymentId, @Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
        Payment payment = paymentService.updatePayment(paymentId, paymentRequestDTO, userId);
        return ResponseEntity.ok(PaymentMapper.toDTO(payment));
    }
}
