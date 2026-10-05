package com.nexly.backend.controller;

import com.nexly.backend.dto.AdminReportResponse;
import com.nexly.backend.entity.AdminReport;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.AdminReportRepository;
import com.nexly.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reports")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminReportController {

    private final AdminReportRepository reportRepository;
    private final UserRepository userRepository;

    public AdminReportController(
            AdminReportRepository reportRepository,
            UserRepository userRepository) {

        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    // GET ALL REPORTS
    @GetMapping
    public ResponseEntity<?> getAllReports(
            Authentication authentication) {

        List<AdminReportResponse> reports =
                reportRepository.findAll()
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(reports);
    }

    // GET OPEN REPORTS
    @GetMapping("/open")
    public ResponseEntity<?> getOpenReports(
            Authentication authentication) {

        List<AdminReportResponse> reports =
                reportRepository
                        .findByStatusOrderByCreatedAtDesc("OPEN")
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(reports);
    }

    // GET RESOLVED REPORTS
    @GetMapping("/resolved")
    public ResponseEntity<?> getResolvedReports(
            Authentication authentication) {

        List<AdminReportResponse> reports =
                reportRepository
                        .findByStatusOrderByCreatedAtDesc("RESOLVED")
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(reports);
    }

    // GET REPORT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getReport(
            @PathVariable Long id,
            Authentication authentication) {

        AdminReport report =
                reportRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Report not found"));

        return ResponseEntity.ok(
                convertToResponse(report)
        );
    }

    // UPDATE REPORT STATUS
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {

        AdminReport report =
                reportRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Report not found"));

        report.setStatus(status);

        if ("RESOLVED".equalsIgnoreCase(status)) {
            report.setResolvedAt(LocalDateTime.now());
        } else {
            report.setResolvedAt(null);
        }

        AdminReport updated =
                reportRepository.save(report);

        return ResponseEntity.ok(
                convertToResponse(updated)
        );
    }

    // DELETE REPORT
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(
            @PathVariable Long id,
            Authentication authentication) {

        AdminReport report =
                reportRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Report not found"));

        reportRepository.delete(report);

        return ResponseEntity.ok(
                "Report deleted successfully"
        );
    }

    // CONVERT ENTITY TO DTO
    private AdminReportResponse convertToResponse(
            AdminReport report) {

        User user = report.getReportedBy();

        Long userId = null;
        String userName = "Unknown";
        String userEmail = "Unknown";

        if (user != null) {
            userId = user.getId();
            userName = user.getName();
            userEmail = user.getEmail();
        }

        return new AdminReportResponse(
                report.getId(),
                userId,
                userName,
                userEmail,
                report.getSubject(),
                report.getDescription(),
                report.getStatus(),
                report.getCreatedAt(),
                report.getResolvedAt()
        );
    }
}