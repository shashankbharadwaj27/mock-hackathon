package com.example.backend.exception;

/**
 * Thrown when an admin attempts an illegal status transition
 * (e.g. moving DELIVERED → PENDING).
 * Maps to HTTP 422 Unprocessable Entity.
 */
public class InvalidOrderStatusException extends PizzaException {

    public InvalidOrderStatusException(String from, String to) {
        super(String.format(
                "Invalid status transition from '%s' to '%s'", from, to));
    }
}