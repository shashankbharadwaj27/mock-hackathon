package com.example.backend.dto.request.catalog;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description; // optional

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Category is required")
    private Long categoryId; // FK → Category

    private String imageUrl; // optional

    @Min(value = 0, message = "Initial stock cannot be negative")
    private int initialStock; // seeds Inventory row
}
