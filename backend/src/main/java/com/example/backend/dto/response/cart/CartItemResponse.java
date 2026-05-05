package com.example.backend.dto.response.cart;

import com.example.backend.dto.response.catalog.ProductResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long cartItemId;
    private ProductResponse product; // nested
    private int quantity;
    private Double subtotal;     // price × qty
}
