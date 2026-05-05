package com.example.backend.dto.request.inventory;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AdjustInventoryRequest {

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity; // absolute value to set

    private String reason; // optional audit note
}
