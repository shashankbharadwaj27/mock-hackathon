package com.example.backend.dto.request.auth;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name; // nullable

    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number")
    private String phone; // nullable

    @Size(min = 8, message = "Current password must be at least 8 characters")
    private String currentPassword; // required if changing password

    @Size(min = 8, message = "New password must be at least 8 characters")
    private String newPassword; // nullable
}
