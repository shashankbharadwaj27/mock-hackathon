package com.example.backend.dto.response.catalog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private boolean isActive;
    private CategoryResponse category; // nested
    private int stockQuantity;         // from Inventory
}
