package com.example.backend.repository;

import com.example.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // ProductService.getProducts()  — paginated listing, all active products
    Page<Product> findByIsActiveTrue(Pageable pageable);

    // ProductService.getProducts()  — paginated listing filtered by category
    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    // findById(Long) and save(entity) are inherited — no declaration needed
}