package com.example.backend.exception;

/**
 * Thrown when a cart/order item requests more quantity
 * than available in Inventory.
 * Maps to HTTP 409 Conflict.
 *
 * Updated: constructors now accept (productId, requested, available)
 * OR a plain message for legacy call-sites in CartService that only
 * have the product name at hand.
 */
public class InsufficientStockException extends PizzaException {

    private Long   productId;
    private int    requested;
    private int    available;

    /** Rich constructor — use in OrderService where all figures are known. */
    public InsufficientStockException(Long productId, int requested, int available) {
        super(String.format(
                "Insufficient stock for product %d: requested %d, available %d",
                productId, requested, available));
        this.productId = productId;
        this.requested = requested;
        this.available = available;
    }

    /** Convenience constructor — use in CartService where only the name is handy. */
    public InsufficientStockException(String productName, int requested, int available) {
        super(String.format(
                "Insufficient stock for '%s': requested %d, available %d",
                productName, requested, available));
        this.requested = requested;
        this.available = available;
    }

    public Long getProductId()  { return productId; }
    public int  getRequested()  { return requested; }
    public int  getAvailable()  { return available; }
}