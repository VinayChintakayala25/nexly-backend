package com.nexly.backend.controller;

import com.nexly.backend.dto.AdminDashboardResponse;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.ApplicationRepository;
import com.nexly.backend.repository.NotificationRepository;
import com.nexly.backend.repository.OpportunityRepository;
import com.nexly.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final OpportunityRepository opportunityRepository;
    private final ApplicationRepository applicationRepository;
    private final NotificationRepository notificationRepository;

    public AdminDashboardController(
            UserRepository userRepository,
            OpportunityRepository opportunityRepository,
            ApplicationRepository applicationRepository,
            NotificationRepository notificationRepository) {

        this.userRepository = userRepository;
        this.opportunityRepository = opportunityRepository;
        this.applicationRepository = applicationRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public ResponseEntity<?> getDashboard(
            Authentication authentication) {

        User admin = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Admin not found"));

        long totalUsers =
                userRepository.count();

        long totalStudents =
                userRepository.countByRole(
                        User.Role.STUDENT
                );

        long totalRecruiters =
                userRepository.countByRole(
                        User.Role.RECRUITER
                );

        long totalOpportunities =
                opportunityRepository.count();

        long publishedOpportunities =
                opportunityRepository.countByStatus(
                        "PUBLISHED"
                );

        long totalApplications =
                applicationRepository.count();

        long selectedApplications =
                applicationRepository.countByStatus(
                        "SELECTED"
                );

        long unreadNotifications =
                notificationRepository.countByUserAndReadFalse(
                        admin
                );

        AdminDashboardResponse response =
                new AdminDashboardResponse(
                        totalUsers,
                        totalStudents,
                        totalRecruiters,
                        totalOpportunities,
                        publishedOpportunities,
                        totalApplications,
                        selectedApplications,
                        unreadNotifications
                );

        return ResponseEntity.ok(response);
    }
}