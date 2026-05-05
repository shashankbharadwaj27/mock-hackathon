package com.example.backend.repository;

import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // AuthService.register()  — checks for duplicate email before saving
    boolean existsByEmail(String email);

    // AuthService.login() / getProfile() / updateProfile()
    // UserService.getUserByEmail()
    Optional<User> findByEmail(String email);

    // UserService.getUserById()
    // findById(Long id) is inherited from JpaRepository — no declaration needed
}