package com.nexly.backend.controller;

import com.nexly.backend.dto.StudentDashboardResponse;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class StudentDashboardController {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final SavedOpportunityRepository savedOpportunityRepository;
    private final InterviewRepository interviewRepository;
    private final NotificationRepository notificationRepository;

    public StudentDashboardController(
            UserRepository userRepository,
            ApplicationRepository applicationRepository,
            SavedOpportunityRepository savedOpportunityRepository,
            InterviewRepository interviewRepository,
            NotificationRepository notificationRepository) {

        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.savedOpportunityRepository = savedOpportunityRepository;
        this.interviewRepository = interviewRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public ResponseEntity<?> getDashboard(
            Authentication authentication) {

        User student = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Student not found"));

        long totalApplications =
                applicationRepository.countByStudent(student);

        long underReview =
                applicationRepository.countByStudentAndStatus(
                        student, "UNDER_REVIEW");

        long shortlisted =
                applicationRepository.countByStudentAndStatus(
                        student, "SHORTLISTED");

        long selected =
                applicationRepository.countByStudentAndStatus(
                        student, "SELECTED");

        long rejected =
                applicationRepository.countByStudentAndStatus(
                        student, "REJECTED");

        long saved =
                savedOpportunityRepository.countByStudent(student);

        long interviews =
                interviewRepository.countByApplicationStudentId(
                        student.getId());

        long unread =
                notificationRepository.countByUserAndReadFalse(student);

        StudentDashboardResponse response =
                new StudentDashboardResponse(
                        totalApplications,
                        underReview,
                        shortlisted,
                        selected,
                        rejected,
                        saved,
                        interviews,
                        unread
                );

        return ResponseEntity.ok(response);
    }
}