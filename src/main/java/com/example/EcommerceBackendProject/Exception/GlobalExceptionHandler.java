package com.example.EcommerceBackendProject.Exception;

import com.example.EcommerceBackendProject.DTO.ApiErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleBadRequestException(BadRequestException ex, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status)
                .body(new ApiErrorResponseDTO(
                        status.value(),
                        status.name(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidOrderItemQuantityException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleInvalidOrderItemQuantityException(InvalidOrderItemQuantityException ex, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleNoResourceFoundException(NoResourceFoundException ex, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                "Invalid value for parameter '" + ex.getName() + "'",
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(NoUserFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleNoUserFoundException(NoUserFoundException ex, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleInvalidOrderStatusException(InvalidOrderStatusException ex, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidPaymentStatusException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleInvalidPaymentStatusException(InvalidPaymentStatusException ex, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidPaymentTypeException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleInvalidPaymentTypeException(InvalidPaymentTypeException ex, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleInvalidRoleException(InvalidRoleException ex, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidSortableFieldsException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleInvalidSortableFieldsException(InvalidSortableFieldsException ex, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpStatus status, HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                message,
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponseDTO> handleException(Exception ex, HttpStatus status, HttpServletRequest request) {
        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                "Unexpected error occurred.",
                request.getRequestURI(),
                LocalDateTime.now()));
    }
}
