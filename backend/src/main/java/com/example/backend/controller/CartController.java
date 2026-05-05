package com.example.backend.controller;

import com.example.backend.dto.request.cart.AddToCartRequest;
import com.example.backend.dto.request.cart.UpdateCartItemRequest;
import com.example.backend.dto.response.cart.CartResponse;
import com.example.backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
public class CartController {

    private final CartService cartService;

    // GET /api/cart  – user
    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getUsername()));
    }

    // POST /api/cart/items  – user
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.addToCart(request, userDetails.getUsername()));
    }

    // PATCH /api/cart/items/:itemId  – user
    @PatchMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.updateCartItem(itemId, request, userDetails.getUsername()));
    }

    // DELETE /api/cart/items/:itemId  – user
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponse> removeCartItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(cartService.removeCartItem(itemId, userDetails.getUsername()));
    }

    // DELETE /api/cart  – user
    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal UserDetails userDetails) {
        cartService.clearCart(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
