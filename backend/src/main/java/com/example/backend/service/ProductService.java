package com.example.backend.service;

import com.example.backend.dto.request.catalog.CreateProductRequest;
import com.example.backend.dto.request.catalog.UpdateProductRequest;
import com.example.backend.dto.response.catalog.CategoryResponse;
import com.example.backend.dto.response.catalog.ProductPageResponse;
import com.example.backend.dto.response.catalog.ProductResponse;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.entity.Category;
import com.example.backend.entity.Inventory;
import com.example.backend.entity.Product;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.InventoryRepository;
import com.example.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;

    // GET /api/categories
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }

    // GET /api/products
    public ProductPageResponse getProducts(Long categoryId, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);

        Page<Product> productPage = (categoryId != null)
                ? productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable)
                : productRepository.findByIsActiveTrue(pageable);

        List<ProductResponse> content = productPage.getContent()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());

        return ProductPageResponse.builder()
                .content(content)
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .build();
    }

    // GET /api/products/:id
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        return mapToProductResponse(product);
    }

    // POST /api/products  – admin
    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setImageUrl(request.getImageUrl());
        product.setIsActive(true);

        productRepository.save(product);

        // seed inventory row
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(request.getInitialStock());
        inventoryRepository.save(inventory);

        return mapToProductResponse(product);
    }

    // PATCH /api/products/:id  – admin
    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        if (request.getName() != null)        product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null)       product.setPrice(request.getPrice());
        if (request.getImageUrl() != null)    product.setImageUrl(request.getImageUrl());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));
            product.setCategory(category);
        }

        productRepository.save(product);
        return mapToProductResponse(product);
    }

    // PATCH /api/products/:id/toggle  – admin
    @Transactional
    public ProductResponse toggleProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
        product.setIsActive(!product.getIsActive());
        productRepository.save(product);
        return mapToProductResponse(product);
    }

    // ── helpers ──────────────────────────────────────────────────
    public ProductResponse mapToProductResponse(Product product) {
        int stock = inventoryRepository.findByProductId(product.getId())
                .map(Inventory::getQuantity)
                .orElse(0);

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .isActive(product.getIsActive())
                .category(mapToCategoryResponse(product.getCategory()))
                .stockQuantity(stock)
                .build();
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .build();
    }
}
