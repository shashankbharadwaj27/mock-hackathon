package com.example.backend.repository;

import com.example.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // ProductService.getAllCategories()  — findAll() inherited from JpaRepository
    // ProductService.createProduct() / updateProduct()  — findById() inherited
    // No custom query methods needed — all calls use inherited JpaRepository methods
}