package com.example.EcommerceBackendProject.Exception;

public class InvalidPaymentStatusException extends RuntimeException {
    public InvalidPaymentStatusException(String message) {
        super(message);
    }
}
