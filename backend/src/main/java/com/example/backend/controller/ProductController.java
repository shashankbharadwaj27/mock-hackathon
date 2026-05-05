package com.example.backend.controller;

import com.example.backend.dto.request.catalog.CreateProductRequest;
import com.example.backend.dto.request.catalog.UpdateProductRequest;
import com.example.backend.dto.response.catalog.ProductPageResponse;
import com.example.backend.dto.response.catalog.ProductResponse;
import com.example.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // GET /api/products?categoryId=&page=&size=  – public
    @GetMapping
    public ResponseEntity<ProductPageResponse> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getProducts(categoryId, page, size));
    }

    // GET /api/products/:id  – public
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // POST /api/products  – admin
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    // PATCH /api/products/:id  – admin
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // PATCH /api/products/:id/toggle  – admin
    @PatchMapping("/{id}/toggle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> toggleProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.toggleProduct(id));
    }
}
