package com.example.backend.controller;

import com.example.backend.dto.request.auth.LoginRequest;
import com.example.backend.dto.request.auth.RegisterRequest;
import com.example.backend.dto.request.auth.UpdateProfileRequest;
import com.example.backend.dto.response.auth.AuthResponse;
import com.example.backend.dto.response.auth.UserProfileResponse;
import com.example.backend.exception.UnauthorizedException;
import com.example.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register  – public
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request received");
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));

    }

    // POST /api/auth/login  – public
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // POST /api/auth/logout  – user
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // GET /api/auth/me  – user
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        return ResponseEntity.ok(authService.getProfile(userDetails.getUsername()));
    }

    // PATCH /api/auth/me  – user
    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        return ResponseEntity.ok(authService.updateProfile(request, userDetails.getUsername()));
    }
}
