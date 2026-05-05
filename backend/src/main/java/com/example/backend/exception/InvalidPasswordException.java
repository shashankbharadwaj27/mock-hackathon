package com.example.backend.exception;

/**
 * Thrown when the current password provided during a profile
 * password-change request does not match the stored hash.
 * Maps to HTTP 400 Bad Request.
 */
public class InvalidPasswordException extends PizzaException {

    public InvalidPasswordException() {
        super("Current password is incorrect");
    }
}