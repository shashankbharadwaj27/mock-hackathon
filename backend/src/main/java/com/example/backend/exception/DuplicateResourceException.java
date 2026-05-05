package com.example.backend.exception;

/**
 * Thrown when a create/register operation violates a uniqueness
 * constraint (e.g. duplicate email on registration).
 * Maps to HTTP 409 Conflict.
 */
public class DuplicateResourceException extends PizzaException {

    public DuplicateResourceException(String resourceName,
                                      String fieldName,
                                      Object fieldValue) {
        super(String.format("%s already exists with %s: '%s'",
                resourceName, fieldName, fieldValue));
    }
}