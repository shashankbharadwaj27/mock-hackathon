package com.example.backend.dto.response.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {

    private Long orderId;
    private String status;
    private BigDecimal totalAmount;
    private int itemCount;
    private LocalDateTime placedAt;
}
