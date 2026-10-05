package com.nexly.backend.controller;

import com.nexly.backend.dto.AdminUserResponse;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(
            UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    // GET ALL USERS
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            Authentication authentication) {

        List<AdminUserResponse> users =
                userRepository.findAll()
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(users);
    }

    // GET ALL STUDENTS
    @GetMapping("/students")
    public ResponseEntity<?> getStudents(
            Authentication authentication) {

        List<AdminUserResponse> students =
                userRepository
                        .findByRole(User.Role.STUDENT)
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(students);
    }

    // GET ALL RECRUITERS
    @GetMapping("/recruiters")
    public ResponseEntity<?> getRecruiters(
            Authentication authentication) {

        List<AdminUserResponse> recruiters =
                userRepository
                        .findByRole(User.Role.RECRUITER)
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(recruiters);
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(
            @PathVariable Long id,
            Authentication authentication) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));

        return ResponseEntity.ok(
                convertToResponse(user)
        );
    }

    // ACTIVATE USER
    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(
            @PathVariable Long id,
            Authentication authentication) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));

        user.setActive(true);

        User updated =
                userRepository.save(user);

        return ResponseEntity.ok(
                convertToResponse(updated)
        );
    }

    // DEACTIVATE USER
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(
            @PathVariable Long id,
            Authentication authentication) {

        User user =
                userRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"));

        user.setActive(false);

        User updated =
                userRepository.save(user);

        return ResponseEntity.ok(
                convertToResponse(updated)
        );
    }

    // CONVERT USER ENTITY TO SAFE DTO
    private AdminUserResponse convertToResponse(
            User user) {

        return new AdminUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getPhone(),
                user.isActive()
        );
    }
}