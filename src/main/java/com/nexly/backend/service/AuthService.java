package com.nexly.backend.service;

import com.nexly.backend.dto.LoginRequest;
import com.nexly.backend.dto.RegisterRequest;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.UserRepository;
import com.nexly.backend.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =========================
    // REGISTER
    // =========================

    public User register(RegisterRequest request) {

        if (request.getName() == null ||
                request.getName().isBlank()) {

            throw new RuntimeException("Name is required");
        }

        if (request.getEmail() == null ||
                request.getEmail().isBlank()) {

            throw new RuntimeException("Email is required");
        }

        if (request.getPassword() == null ||
                request.getPassword().length() < 6) {

            throw new RuntimeException(
                    "Password must contain at least 6 characters"
            );
        }

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new RuntimeException(
                    "Email already registered"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );
        user.setPhone(request.getPhone());

        if (request.getRole() == null) {
            user.setRole(User.Role.STUDENT);
        } else {
            user.setRole(request.getRole());
        }

        user.setActive(true);

        return userRepository.save(user);
    }

    // =========================
    // LOGIN
    // =========================

    public String login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid email or password"
                        ));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid email or password"
            );
        }

        if (!user.isActive()) {

            throw new RuntimeException(
                    "User account is inactive"
            );
        }

        return jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
    }
}