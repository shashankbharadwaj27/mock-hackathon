package com.example.backend.repository;

import com.example.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // OrderService.getOrders()  — admin path: all orders newest-first
    List<Order> findAllByOrderByPlacedAtDesc();

    // OrderService.getOrders()  — user path: only the caller's orders newest-first
    List<Order> findByUserIdOrderByPlacedAtDesc(Long userId);

    // findById(Long) and save(entity) are inherited — no declaration needed
}