package com.example.backend.exception;

/**
 * Thrown when a requested resource (Product, Order, User, etc.)
 * is not found in the database.
 * Maps to HTTP 404 Not Found.
 */
public class ResourceNotFoundException extends PizzaException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}