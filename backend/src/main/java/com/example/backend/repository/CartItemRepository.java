package com.example.backend.repository;

import com.example.backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // CartService.addToCart()  — finds existing item before deciding add vs update
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    // CartService.clearCart()  — bulk-deletes all items for a cart
    // deleteAll(Iterable) is inherited from JpaRepository — no declaration needed

    // CartService.updateCartItem() / removeCartItem()
    // findById(Long) and delete(entity) are inherited — no declaration needed
}