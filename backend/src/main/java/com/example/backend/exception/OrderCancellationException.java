package com.example.backend.exception;

/**
 * Thrown when an order cannot be cancelled because it has
 * already been dispatched or delivered.
 * Maps to HTTP 409 Conflict.
 */
public class OrderCancellationException extends PizzaException {

    private final Long   orderId;
    private final String currentStatus;

    public OrderCancellationException(Long orderId, String currentStatus) {
        super(String.format(
                "Order %d cannot be cancelled. Current status: %s",
                orderId, currentStatus));
        this.orderId       = orderId;
        this.currentStatus = currentStatus;
    }

    public Long   getOrderId()       { return orderId; }
    public String getCurrentStatus() { return currentStatus; }
}