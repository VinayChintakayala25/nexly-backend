package com.nexly.backend.controller;

import com.nexly.backend.dto.ApplicationRequest;
import com.nexly.backend.entity.Application;
import com.nexly.backend.entity.Company;
import com.nexly.backend.entity.Opportunity;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.ApplicationRepository;
import com.nexly.backend.repository.CompanyRepository;
import com.nexly.backend.repository.OpportunityRepository;
import com.nexly.backend.repository.UserRepository;
import com.nexly.backend.service.AuditLogService;
import com.nexly.backend.service.NotificationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "http://localhost:5173")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public ApplicationController(
            ApplicationRepository applicationRepository,
            OpportunityRepository opportunityRepository,
            UserRepository userRepository,
            CompanyRepository companyRepository,
            NotificationService notificationService,
            AuditLogService auditLogService) {

        this.applicationRepository = applicationRepository;
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    // =========================================================
    // STUDENT: Apply for an opportunity
    // =========================================================

    @PostMapping
    public ResponseEntity<?> apply(
            Authentication authentication,
            @RequestBody ApplicationRequest request) {

        // =====================================================
        // FIND LOGGED-IN STUDENT
        // =====================================================

        User student = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // =====================================================
        // CHECK STUDENT ROLE
        // =====================================================

        if (student.getRole() != User.Role.STUDENT) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            "Only students can apply for opportunities"
                    );
        }

        // =====================================================
        // CHECK OPPORTUNITY ID
        // =====================================================

        if (request.getOpportunityId() == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Opportunity ID is required");
        }

        // =====================================================
        // FIND OPPORTUNITY
        // =====================================================

        Opportunity opportunity =
                opportunityRepository
                        .findById(
                                request.getOpportunityId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Opportunity not found"
                                ));

        // =====================================================
        // CHECK OPPORTUNITY STATUS
        // =====================================================

        if (!"PUBLISHED".equalsIgnoreCase(
                opportunity.getStatus())) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "This opportunity is not open"
                    );
        }

        // =====================================================
        // CHECK APPLICATION DEADLINE
        // =====================================================

        LocalDateTime deadline =
                opportunity.getApplicationDeadline();

        if (deadline != null &&
                LocalDateTime.now().isAfter(deadline)) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "The application deadline for this opportunity has passed."
                    );
        }

        // =====================================================
        // CHECK COMPANY
        // =====================================================

        if (opportunity.getCompany() == null) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "This opportunity has no company"
                    );
        }

        // =====================================================
        // CHECK DUPLICATE APPLICATION
        // =====================================================

        if (applicationRepository
                .findByStudentAndOpportunity(
                        student,
                        opportunity
                )
                .isPresent()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "You already applied"
                    );
        }

        // =====================================================
        // CREATE APPLICATION
        // =====================================================

        Application application =
                new Application();

        application.setStudent(student);

        application.setOpportunity(
                opportunity
        );

        application.setCoverLetter(
                request.getCoverLetter()
        );

        application.setResumeUrl(
                request.getResumeUrl()
        );

        application.setStatus(
                "APPLIED"
        );

        // =====================================================
        // SAVE APPLICATION
        // =====================================================

        Application savedApplication =
                applicationRepository.save(
                        application
                );

        // =====================================================
        // AUDIT LOG
        // =====================================================

        auditLogService.log(
                student,
                "APPLICATION_CREATED",
                student.getName()
                        + " applied for "
                        + opportunity.getTitle(),
                "APPLICATION",
                savedApplication.getId()
        );

        // =====================================================
        // SEND NOTIFICATION TO RECRUITER
        // =====================================================

        User recruiter =
                opportunity
                        .getCompany()
                        .getRecruiter();

        if (recruiter != null) {

            notificationService.createNotification(
                    recruiter,
                    student.getName()
                            + " applied for "
                            + opportunity.getTitle(),
                    "NEW_APPLICATION"
            );
        }

        // =====================================================
        // SUCCESS RESPONSE
        // =====================================================

        return ResponseEntity.ok(
                savedApplication
        );
    }


    // =========================================================
    // STUDENT: View my applications
    // =========================================================

    @GetMapping("/student")
    public ResponseEntity<?> getStudentApplications(
            Authentication authentication) {

        User student = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));

        if (student.getRole() != User.Role.STUDENT) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            "Only students can access this endpoint"
                    );
        }

        return ResponseEntity.ok(
                applicationRepository.findByStudent(
                        student
                )
        );
    }


    // =========================================================
    // RECRUITER: View applicants for an opportunity
    // =========================================================

    @GetMapping("/opportunity/{opportunityId}")
    public ResponseEntity<?> getApplicants(
            @PathVariable Long opportunityId,
            Authentication authentication) {

        User recruiter = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Recruiter not found"
                        ));

        if (recruiter.getRole() != User.Role.RECRUITER) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            "Only recruiters can view applicants"
                    );
        }

        Company company = companyRepository
                .findByRecruiter(recruiter)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Company profile not found"
                        ));

        Opportunity opportunity =
                opportunityRepository
                        .findById(opportunityId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Opportunity not found"
                                ));

        // =====================================================
        // CHECK OPPORTUNITY OWNERSHIP
        // =====================================================

        if (opportunity.getCompany() == null ||
                !opportunity
                        .getCompany()
                        .getId()
                        .equals(company.getId())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            "You are not authorized to view " +
                                    "these applications"
                    );
        }

        return ResponseEntity.ok(
                applicationRepository
                        .findByOpportunity(
                                opportunity
                        )
        );
    }


    // =========================================================
    // RECRUITER: Change application status
    // =========================================================

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {

        // =====================================================
        // FIND RECRUITER
        // =====================================================

        User recruiter = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));

        // =====================================================
        // CHECK RECRUITER ROLE
        // =====================================================

        if (recruiter.getRole() != User.Role.RECRUITER) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            "Only recruiters can change " +
                                    "application status"
                    );
        }

        // =====================================================
        // FIND RECRUITER COMPANY
        // =====================================================

        Company company = companyRepository
                .findByRecruiter(recruiter)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Company profile not found"
                        ));

        // =====================================================
        // FIND APPLICATION
        // =====================================================

        Application application =
                applicationRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"
                                ));

        Opportunity opportunity =
                application.getOpportunity();

        // =====================================================
        // CHECK OWNERSHIP
        // =====================================================

        if (opportunity == null ||
                opportunity.getCompany() == null ||
                !opportunity
                        .getCompany()
                        .getId()
                        .equals(company.getId())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            "You are not authorized to update " +
                                    "this application"
                    );
        }

        // =====================================================
        // VALIDATE STATUS
        // =====================================================

        if (status == null ||
                status.isBlank()) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Application status is required"
                    );
        }

        String newStatus =
                status
                        .trim()
                        .toUpperCase();

        Set<String> allowedStatuses =
                Set.of(
                        "APPLIED",
                        "UNDER_REVIEW",
                        "SHORTLISTED",
                        "INTERVIEW_SCHEDULED",
                        "SELECTED",
                        "REJECTED",
                        "WITHDRAWN"
                );

        if (!allowedStatuses.contains(
                newStatus)) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Invalid application status. " +
                                    "Allowed values: APPLIED, UNDER_REVIEW, " +
                                    "SHORTLISTED, INTERVIEW_SCHEDULED, " +
                                    "SELECTED, REJECTED, WITHDRAWN"
                    );
        }

        // =====================================================
        // SAVE OLD STATUS
        // =====================================================

        String oldStatus =
                application.getStatus();

        // =====================================================
        // UPDATE STATUS
        // =====================================================

        application.setStatus(
                newStatus
        );

        Application savedApplication =
                applicationRepository.save(
                        application
                );

        // =====================================================
        // AUDIT LOG
        // =====================================================

        auditLogService.log(
                recruiter,
                "APPLICATION_STATUS_CHANGED",
                "Application status changed from "
                        + oldStatus
                        + " to "
                        + newStatus
                        + " for "
                        + opportunity.getTitle(),
                "APPLICATION",
                application.getId()
        );

        // =====================================================
        // SEND NOTIFICATION TO STUDENT
        // =====================================================

        User student =
                application.getStudent();

        if (student != null) {

            notificationService.createNotification(
                    student,
                    "Your application for "
                            + opportunity.getTitle()
                            + " is now "
                            + newStatus,
                    "APPLICATION_STATUS"
            );
        }

        // =====================================================
        // SUCCESS RESPONSE
        // =====================================================

        return ResponseEntity.ok(
                savedApplication
        );
    }
}