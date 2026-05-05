package com.example.backend.exception;
/**
 * Thrown when a user attempts to add an inactive/deactivated
 * product to their cart.
 * Maps to HTTP 400 Bad Request.
 */
public class ProductInactiveException extends PizzaException {

    private final String productName;

    public ProductInactiveException(String productName) {
        super(String.format(
                "Product '%s' is currently unavailable", productName));
        this.productName = productName;
    }

    public String getProductName() { return productName; }
}