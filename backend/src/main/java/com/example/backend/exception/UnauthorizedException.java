package com.example.backend.exception;

/**
 * Thrown when a user attempts to access or mutate a resource
 * they do not own (e.g. viewing another user's order).
 * Maps to HTTP 403 Forbidden.
 */
public class UnauthorizedException extends PizzaException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("Access denied: you do not have permission to perform this action");
    }
}