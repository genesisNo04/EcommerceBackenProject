package com.example.EcommerceBackendProject.Exception;

import com.example.EcommerceBackendProject.DTO.ApiErrorResponseDTO;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleBadRequestException(BadRequestException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status)
                .body(new ApiErrorResponseDTO(
                        status.value(),
                        status.name(),
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()));
    }

    @ExceptionHandler(UserAccessDeniedException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleUserAccessDeniedException(UserAccessDeniedException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidOrderItemQuantityException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleInvalidOrderItemQuantityException(InvalidOrderItemQuantityException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                "Invalid value for parameter '" + ex.getName() + "'",
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(NoUserFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleNoUserFoundException(NoUserFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleInvalidOrderStatusException(InvalidOrderStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidPaymentStatusException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleInvalidPaymentStatusException(InvalidPaymentStatusException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidPaymentTypeException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleInvalidPaymentTypeException(InvalidPaymentTypeException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleInvalidRoleException(InvalidRoleException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(InvalidSortableFieldsException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleInvalidSortableFieldsException(InvalidSortableFieldsException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

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
    public ResponseEntity<ApiErrorResponseDTO> handleException(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleDbError(DataIntegrityViolationException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleIllegalStateException(IllegalStateException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()));
    }

    @ExceptionHandler({OptimisticLockingFailureException.class, OptimisticLockException.class})
    public ResponseEntity<ApiErrorResponseDTO> handleOptimisticLockingException(Exception ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        return ResponseEntity.status(status).body(new ApiErrorResponseDTO(
                status.value(),
                status.name(),
                "CONCURRENT_MODIFICATION",
                request.getRequestURI(),
                LocalDateTime.now()));
    }
}
