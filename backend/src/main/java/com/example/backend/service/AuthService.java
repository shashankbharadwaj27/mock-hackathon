package com.example.backend.service;

import com.example.backend.dto.request.auth.LoginRequest;
import com.example.backend.dto.request.auth.RegisterRequest;
import com.example.backend.dto.request.auth.UpdateProfileRequest;
import com.example.backend.dto.response.auth.AuthResponse;
import com.example.backend.dto.response.auth.UserProfileResponse;
import com.example.backend.entity.Role;
import com.example.backend.entity.User;
import com.example.backend.exception.DuplicateResourceException;
import com.example.backend.exception.InvalidPasswordException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // POST /api/auth/register
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }
        log.info("Register request received to service");
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setRole(Role.USER);
        log.info("Register request before saving");
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail());
        return buildAuthResponse(user, token);
    }

    // POST /api/auth/login
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtUtil.generateToken(auth.getName());
        return buildAuthResponse(user, token);
    }

    // POST /api/auth/logout
    public void logout(String email) {
        // Stateless JWT — token expiry handles invalidation.
        // Add Redis blacklist here when needed.
    }

    // GET /api/auth/me
    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
        return mapToProfileResponse(user);
    }

    // PATCH /api/auth/me
    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        if (request.getName()  != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());

        if (request.getNewPassword() != null) {
            if (request.getCurrentPassword() == null ||
                    !passwordEncoder.matches(request.getCurrentPassword(),
                            user.getPasswordHash())) {
                throw new InvalidPasswordException();
            }
            user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
        return mapToProfileResponse(user);
    }

    // ── helpers ──────────────────────────────────────────────────────
    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationMs() / 1000)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    private UserProfileResponse mapToProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
    }
}