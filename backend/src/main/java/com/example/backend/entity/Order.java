package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;
import  com.example.backend.entity.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OrderStatus status;

    private Double totalAmount;

    private String deliveryAddress;

    private LocalDateTime placedAt;

    private String paymentStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
}