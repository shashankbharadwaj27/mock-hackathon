package com.example.backend.dto.request.order;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PlaceOrderRequest {

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    private String paymentMethod; // COD | ONLINE

    private String notes; // optional delivery notes
}
