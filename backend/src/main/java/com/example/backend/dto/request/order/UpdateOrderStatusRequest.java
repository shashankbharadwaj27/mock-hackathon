package com.example.backend.dto.request.order;

import com.example.backend.entity.OrderStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateOrderStatusRequest {

    @NotNull(message = "Status is required")
    private OrderStatus status;
    // PENDING → CONFIRMED → PREPARING → DISPATCHED → DELIVERED | CANCELLED

    private String note; // optional admin note
}
