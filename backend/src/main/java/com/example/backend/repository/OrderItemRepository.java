package com.example.backend.repository;

import com.example.backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // OrderService.placeOrder()  — bulk-saves all items in one call
    // saveAll(Iterable) is inherited from JpaRepository — no declaration needed

    // OrderService accesses items via order.getOrderItems() (lazy collection)
    // so no findByOrderId() query is needed here
}