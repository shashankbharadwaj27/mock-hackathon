package com.example.backend.exception;

/**
 * Thrown when a user attempts to place an order from an empty cart.
 * Maps to HTTP 400 Bad Request.
 */
public class EmptyCartException extends PizzaException {

    public EmptyCartException() {
        super("Cannot place order: cart is empty");
    }
}