package com.example.backend.exception;

/**
 * Base unchecked exception for all Pizza application errors.
 * Extend this class for all domain-specific exceptions.
 */
public class PizzaException extends RuntimeException {

    public PizzaException(String message) {
        super(message);
    }

    public PizzaException(String message, Throwable cause) {
        super(message, cause);
    }
}