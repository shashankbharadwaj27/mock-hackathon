package com.example.backend.dto.response.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tokenType; // "Bearer"
    private long expiresIn;   // seconds
    private Long userId;
    private String name;
    private String email;
    private String role;      // USER | ADMIN
}
