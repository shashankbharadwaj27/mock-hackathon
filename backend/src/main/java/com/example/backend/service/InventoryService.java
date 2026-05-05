package com.example.backend.service;

import com.example.backend.dto.request.inventory.AdjustInventoryRequest;
import com.example.backend.dto.response.inventory.InventoryResponse;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.entity.Inventory;
import com.example.backend.repository.InventoryRepository;
import com.example.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    // GET /api/inventory
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // GET /api/inventory/:productId
    public InventoryResponse getInventoryByProduct(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));
        return mapToResponse(inventory);
    }

    // PATCH /api/inventory/:productId
    @Transactional
    public InventoryResponse adjustInventory(Long productId, AdjustInventoryRequest request) {
        // ensure product exists
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        inventory.setQuantity(request.getQuantity());
        inventory.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inventory);

        return mapToResponse(inventory);
    }

    // used by CartService and OrderService to validate stock
    public int getAvailableStock(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(Inventory::getQuantity)
                .orElse(0);
    }

    // ── helpers ──────────────────────────────────────────────────
    private InventoryResponse mapToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .quantity(inventory.getQuantity())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}
