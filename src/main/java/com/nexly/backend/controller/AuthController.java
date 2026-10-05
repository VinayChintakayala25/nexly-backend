package com.nexly.backend.controller;

import com.nexly.backend.dto.LoginRequest;
import com.nexly.backend.dto.RegisterRequest;
import com.nexly.backend.entity.User;
import com.nexly.backend.service.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    // =====================================================
    // EMAIL VALIDATION
    // =====================================================

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
            );

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // =====================================================
    // REGISTER
    // =====================================================

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request) {

        try {

            // =================================================
            // NAME VALIDATION
            // =================================================

            if (request.getName() == null ||
                    request.getName().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(createError(
                                "Full name is required"
                        ));
            }

            // =================================================
            // EMAIL VALIDATION
            // =================================================

            if (request.getEmail() == null ||
                    request.getEmail().trim().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(createError(
                                "Email address is required"
                        ));
            }

            String email =
                    request.getEmail()
                            .trim()
                            .toLowerCase();

            if (!EMAIL_PATTERN.matcher(email).matches()) {

                return ResponseEntity
                        .badRequest()
                        .body(createError(
                                "Please enter a valid email address"
                        ));
            }

            // Store cleaned email back into request
            request.setEmail(email);

            // =================================================
            // PASSWORD VALIDATION
            // =================================================

            if (request.getPassword() == null ||
                    request.getPassword().isEmpty()) {

                return ResponseEntity
                        .badRequest()
                        .body(createError(
                                "Password is required"
                        ));
            }

            if (request.getPassword().length() < 6) {

                return ResponseEntity
                        .badRequest()
                        .body(createError(
                                "Password must contain at least 6 characters"
                        ));
            }

            // =================================================
            // REGISTER USER
            // =================================================

            User user = authService.register(request);

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Registration successful"
            );

            response.put(
                    "id",
                    user.getId()
            );

            response.put(
                    "name",
                    user.getName()
            );

            response.put(
                    "email",
                    user.getEmail()
            );

            response.put(
                    "role",
                    user.getRole()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            Map<String, String> response =
                    new HashMap<>();

            response.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }
    }

    // =====================================================
    // LOGIN
    // =====================================================

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {

        try {

            String token =
                    authService.login(request);

            Map<String, Object> response =
                    new HashMap<>();

            response.put(
                    "message",
                    "Login successful"
            );

            response.put(
                    "token",
                    token
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {

            Map<String, String> response =
                    new HashMap<>();

            response.put(
                    "error",
                    e.getMessage()
            );

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }
    }

    // =====================================================
    // ERROR RESPONSE
    // =====================================================

    private Map<String, String> createError(
            String message) {

        Map<String, String> response =
                new HashMap<>();

        response.put(
                "error",
                message
        );

        return response;
    }
}