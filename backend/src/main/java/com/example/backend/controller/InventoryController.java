package com.example.backend.controller;

import com.example.backend.dto.request.inventory.AdjustInventoryRequest;
import com.example.backend.dto.response.inventory.InventoryResponse;
import com.example.backend.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class InventoryController {

    private final InventoryService inventoryService;

    // GET /api/inventory  – admin
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    // GET /api/inventory/:productId  – admin
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProduct(productId));
    }

    // PATCH /api/inventory/:productId  – admin
    @PatchMapping("/{productId}")
    public ResponseEntity<InventoryResponse> adjustInventory(
            @PathVariable Long productId,
            @Valid @RequestBody AdjustInventoryRequest request) {
        return ResponseEntity.ok(inventoryService.adjustInventory(productId, request));
    }
}
