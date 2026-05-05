package com.example.backend.dto.request.catalog;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateProductRequest {

    @Size(min = 1, message = "Name cannot be blank")
    private String name; // nullable

    private String description; // nullable

    @Positive(message = "Price must be positive")
    private Double price; // nullable

    private Long categoryId; // nullable

    private String imageUrl; // nullable
}
