package com.example.backend.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long orderId;
    private String status;           // OrderStatus enum value
    private String deliveryAddress;
    private BigDecimal totalAmount;
    private String paymentStatus;    // PENDING | PAID | FAILED
    private LocalDateTime placedAt;
    private List<OrderItemResponse> items; // nested
}
