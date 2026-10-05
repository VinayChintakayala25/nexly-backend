package com.nexly.backend.controller;

import com.nexly.backend.dto.InterviewRequest;
import com.nexly.backend.entity.Application;
import com.nexly.backend.entity.Company;
import com.nexly.backend.entity.Interview;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.ApplicationRepository;
import com.nexly.backend.repository.CompanyRepository;
import com.nexly.backend.repository.InterviewRepository;
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
@RequestMapping("/api/interviews")
@CrossOrigin(origins = "http://localhost:5173")
public class InterviewController {

    private final InterviewRepository interviewRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public InterviewController(
            InterviewRepository interviewRepository,
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            CompanyRepository companyRepository,
            NotificationService notificationService,
            AuditLogService auditLogService) {

        this.interviewRepository = interviewRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    // =========================================================
    // RECRUITER: Schedule interview
    // =========================================================
    @PostMapping
    public ResponseEntity<?> scheduleInterview(
            Authentication authentication,
            @RequestBody InterviewRequest request) {

        User recruiter = getUser(authentication);

        // Only recruiters can schedule interviews
        if (recruiter.getRole() != User.Role.RECRUITER) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Only recruiters can schedule interviews");
        }

        if (request.getApplicationId() == null) {
            return ResponseEntity.badRequest()
                    .body("Application ID is required");
        }

        if (request.getInterviewDateTime() == null) {
            return ResponseEntity.badRequest()
                    .body("Interview date and time are required");
        }

        // Interview must be scheduled for a future date/time
        if (request.getInterviewDateTime()
                .isBefore(LocalDateTime.now())) {

            return ResponseEntity.badRequest()
                    .body("Interview date and time must be in the future");
        }

        if (request.getMode() == null ||
                request.getMode().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Interview mode is required");
        }

        Set<String> allowedModes = Set.of(
                "ONLINE",
                "OFFLINE",
                "HYBRID"
        );

        String mode = request.getMode()
                .trim()
                .toUpperCase();

        if (!allowedModes.contains(mode)) {
            return ResponseEntity.badRequest()
                    .body(
                            "Invalid interview mode. " +
                                    "Allowed values: ONLINE, OFFLINE, HYBRID"
                    );
        }

        // =====================================================
        // FIND APPLICATION
        // =====================================================

        Application application =
                applicationRepository.findById(
                        request.getApplicationId()
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Application not found"));

        if (application.getOpportunity() == null) {
            return ResponseEntity.badRequest()
                    .body("Application has no opportunity");
        }

        if (application.getOpportunity().getCompany() == null) {
            return ResponseEntity.badRequest()
                    .body("Opportunity has no company");
        }

        // =====================================================
        // GET RECRUITER COMPANY
        // =====================================================

        Company company = companyRepository
                .findByRecruiter(recruiter)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Company profile not found"));

        // =====================================================
        // SECURITY CHECK
        // =====================================================

        if (!application.getOpportunity()
                .getCompany()
                .getId()
                .equals(company.getId())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            "You are not authorized to schedule " +
                                    "an interview for this application"
                    );
        }

        // Check student exists
        if (application.getStudent() == null) {
            return ResponseEntity.badRequest()
                    .body("Application has no student");
        }

        // =====================================================
        // PREVENT DUPLICATE INTERVIEW
        // =====================================================

        if (interviewRepository
                .findByApplication(application)
                .isPresent()) {

            return ResponseEntity.badRequest()
                    .body("Interview already scheduled");
        }

        // =====================================================
        // CREATE INTERVIEW
        // =====================================================

        Interview interview = new Interview();

        interview.setApplication(application);

        interview.setInterviewDateTime(
                request.getInterviewDateTime()
        );

        interview.setMode(mode);

        interview.setMeetingLink(
                request.getMeetingLink()
        );

        interview.setNotes(
                request.getNotes()
        );

        interview.setStatus("SCHEDULED");

        Interview savedInterview =
                interviewRepository.save(interview);

        // =====================================================
        // AUDIT LOG
        // =====================================================

        auditLogService.log(
                recruiter,
                "INTERVIEW_SCHEDULED",
                "Interview scheduled for "
                        + application.getStudent().getName()
                        + " for "
                        + application.getOpportunity().getTitle(),
                "INTERVIEW",
                savedInterview.getId()
        );

        // =====================================================
        // UPDATE APPLICATION STATUS
        // =====================================================

        application.setStatus("INTERVIEW_SCHEDULED");

        applicationRepository.save(application);

        // =====================================================
        // NOTIFY STUDENT
        // =====================================================

        User student = application.getStudent();

        notificationService.createNotification(
                student,
                "Interview scheduled for "
                        + application.getOpportunity().getTitle()
                        + " on "
                        + request.getInterviewDateTime(),
                "INTERVIEW_SCHEDULED"
        );

        return ResponseEntity.ok(savedInterview);
    }

    // =========================================================
    // STUDENT: View my interviews
    // =========================================================
    @GetMapping("/student")
    public ResponseEntity<?> getStudentInterviews(
            Authentication authentication) {

        User student = getUser(authentication);

        if (student.getRole() != User.Role.STUDENT) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Only students can access this endpoint");
        }

        return ResponseEntity.ok(
                interviewRepository
                        .findByApplicationStudentId(
                                student.getId()
                        )
        );
    }

    // =========================================================
    // RECRUITER: View my scheduled interviews
    // =========================================================
    @GetMapping("/recruiter")
    public ResponseEntity<?> getRecruiterInterviews(
            Authentication authentication) {

        User recruiter = getUser(authentication);

        if (recruiter.getRole() != User.Role.RECRUITER) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Only recruiters can access this endpoint");
        }

        companyRepository
                .findByRecruiter(recruiter)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Company profile not found"));

        return ResponseEntity.ok(
                interviewRepository
                        .findByApplicationOpportunityCompanyRecruiterId(
                                recruiter.getId()
                        )
        );
    }

    // =========================================================
    // GET CURRENT USER
    // =========================================================
    private User getUser(
            Authentication authentication) {

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"));
    }
}