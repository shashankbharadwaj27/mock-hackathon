package com.example.backend.repository;

import com.example.backend.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    // Used in 4 services:
    // InventoryService.getInventoryByProduct() / adjustInventory()
    // ProductService.mapToProductResponse()  — reads stock to include in response
    // CartService.addToCart() / updateCartItem()  — via InventoryService.getAvailableStock()
    // OrderService.placeOrder() / cancelOrder()  — stock validation and deduction/restore
    Optional<Inventory> findByProductId(Long productId);

    // findAll() and save(entity) are inherited — no declaration needed
}