package com.example.backend.exception;

/**
 * Thrown when a user attempts to update or remove a cart item
 * that does not belong to their cart.
 * Maps to HTTP 403 Forbidden.
 */
public class CartOwnershipException extends PizzaException {

    public CartOwnershipException(Long itemId) {
        super(String.format(
                "Cart item %d does not belong to the current user", itemId));
    }
}