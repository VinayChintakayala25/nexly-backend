package com.nexly.backend.controller;

import com.nexly.backend.dto.StudentProfileRequest;
import com.nexly.backend.entity.StudentProfile;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.StudentProfileRepository;
import com.nexly.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/student/profile")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentProfileController {

    private final StudentProfileRepository profileRepository;
    private final UserRepository userRepository;

    public StudentProfileController(
            StudentProfileRepository profileRepository,
            UserRepository userRepository) {

        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // CREATE / UPDATE PROFILE
    // ==========================================

    @PostMapping
    public ResponseEntity<?> saveProfile(
            Authentication authentication,
            @RequestBody StudentProfileRequest request) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"
                        ));

        // Update user's basic information
        if (request.getFullName() != null &&
                !request.getFullName().trim().isEmpty()) {

            user.setName(
                    request.getFullName().trim()
            );
        }

        if (request.getPhone() != null) {
            user.setPhone(
                    request.getPhone().trim()
            );
        }

        userRepository.save(user);

        // Find existing profile
        StudentProfile profile =
                profileRepository
                        .findByUser(user)
                        .orElseGet(StudentProfile::new);

        // Connect profile with logged-in user
        profile.setUser(user);

        // Save profile information
        profile.setCollege(
                request.getCollege()
        );

        profile.setDegree(
                request.getDegree()
        );

        profile.setDepartment(
                request.getDepartment()
        );

        profile.setYear(
                request.getYear()
        );

        profile.setGraduationYear(
                request.getGraduationYear()
        );

        profile.setLocation(
                request.getLocation()
        );

        profile.setBio(
                request.getBio()
        );

        profile.setCareerObjective(
                request.getCareerObjective()
        );

        profile.setGithub(
                request.getGithubUrl()
        );

        profile.setLinkedin(
                request.getLinkedinUrl()
        );

        profile.setPortfolio(
                request.getPortfolioUrl()
        );

        StudentProfile savedProfile =
                profileRepository.save(profile);

        return ResponseEntity.ok(
                buildProfileResponse(
                        user,
                        savedProfile
                )
        );
    }

    // ==========================================
    // GET PROFILE
    // ==========================================

    @GetMapping
    public ResponseEntity<?> getProfile(
            Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Logged-in user not found"
                        ));

        return profileRepository
                .findByUser(user)
                .map(profile ->
                        ResponseEntity.ok(
                                buildProfileResponse(
                                        user,
                                        profile
                                )
                        )
                )
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // ==========================================
    // RESPONSE BUILDER
    // ==========================================

    private Map<String, Object> buildProfileResponse(
            User user,
            StudentProfile profile) {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "id",
                profile.getId()
        );

        response.put(
                "fullName",
                user.getName()
        );

        response.put(
                "phone",
                user.getPhone()
        );

        response.put(
                "college",
                profile.getCollege()
        );

        response.put(
                "degree",
                profile.getDegree()
        );

        response.put(
                "department",
                profile.getDepartment()
        );

        response.put(
                "year",
                profile.getYear()
        );

        response.put(
                "graduationYear",
                profile.getGraduationYear()
        );

        response.put(
                "location",
                profile.getLocation()
        );

        response.put(
                "bio",
                profile.getBio()
        );

        response.put(
                "careerObjective",
                profile.getCareerObjective()
        );

        response.put(
                "githubUrl",
                profile.getGithub()
        );

        response.put(
                "linkedinUrl",
                profile.getLinkedin()
        );

        response.put(
                "portfolioUrl",
                profile.getPortfolio()
        );

        return response;
    }
}