package com.example.EcommerceBackendProject.Exception;

public class InvalidOrderItemQuantityException extends RuntimeException {
    public InvalidOrderItemQuantityException(String message) {
        super(message);
    }
}
