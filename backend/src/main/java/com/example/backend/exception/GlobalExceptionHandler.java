package com.example.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // ─── 404 ────────────────────────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // ─── 409 Conflict ───────────────────────────────────────────
    @ExceptionHandler({
            InsufficientStockException.class,
            OrderCancellationException.class,
            DuplicateResourceException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(PizzaException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    // ─── 422 ────────────────────────────────────────────────────
    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ErrorResponse> handleUnprocessable(
            InvalidOrderStatusException ex) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    // ─── 403 Forbidden ──────────────────────────────────────────
    @ExceptionHandler({
            UnauthorizedException.class,
            CartOwnershipException.class
    })
    public ResponseEntity<ErrorResponse> handleForbidden(PizzaException ex) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // ─── 400 Bad Request ────────────────────────────────────────
    @ExceptionHandler({
            EmptyCartException.class,
            ProductInactiveException.class,
            InvalidPasswordException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(PizzaException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ─── Validation (@Valid) ────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errors.put(fe.getField(), fe.getDefaultMessage()));
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation failed",
                errors.toString(),
                LocalDateTime.now());
        return ResponseEntity.badRequest().body(body);
    }

    // ─── Fallback ────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {

        log.error("Unhandled exception occurred", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(
                new ErrorResponse(status.value(), status.getReasonPhrase(),
                        message, LocalDateTime.now()));
    }

    public record ErrorResponse(
            int           status,
            String        error,
            String        message,
            LocalDateTime timestamp
    ) {}
}

