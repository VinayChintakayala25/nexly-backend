package com.nexly.backend.controller;

import com.nexly.backend.dto.AdminReportRequest;
import com.nexly.backend.entity.AdminReport;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.AdminReportRepository;
import com.nexly.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class ReportController {

    private final AdminReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportController(
            AdminReportRepository reportRepository,
            UserRepository userRepository) {

        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // STUDENT / RECRUITER: Submit report
    // =========================================================
    @PostMapping
    public ResponseEntity<?> createReport(
            Authentication authentication,
            @RequestBody AdminReportRequest request) {

        User user = getUser(authentication);

        if (request.getSubject() == null ||
                request.getSubject().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Report subject is required");
        }

        if (request.getDescription() == null ||
                request.getDescription().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Report description is required");
        }

        if (request.getSubject().length() > 200) {
            return ResponseEntity.badRequest()
                    .body("Subject cannot exceed 200 characters");
        }

        if (request.getDescription().length() > 2000) {
            return ResponseEntity.badRequest()
                    .body("Description cannot exceed 2000 characters");
        }

        AdminReport report = new AdminReport();

        report.setReportedBy(user);
        report.setSubject(request.getSubject().trim());
        report.setDescription(request.getDescription().trim());
        report.setStatus("OPEN");

        AdminReport savedReport =
                reportRepository.save(report);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedReport);
    }

    // =========================================================
    // USER: View my submitted reports
    // =========================================================
    @GetMapping("/my")
    public ResponseEntity<?> getMyReports(
            Authentication authentication) {

        User user = getUser(authentication);

        List<AdminReport> reports =
                reportRepository
                        .findByReportedByOrderByCreatedAtDesc(user);

        return ResponseEntity.ok(reports);
    }

    // =========================================================
    // Get current user
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