package com.nexly.backend.controller;

import com.nexly.backend.dto.AdminApplicationResponse;
import com.nexly.backend.entity.Application;
import com.nexly.backend.repository.ApplicationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/applications")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminApplicationController {

    private final ApplicationRepository applicationRepository;

    public AdminApplicationController(
            ApplicationRepository applicationRepository) {

        this.applicationRepository = applicationRepository;
    }

    // GET ALL APPLICATIONS
    @GetMapping
    public ResponseEntity<?> getAllApplications(
            Authentication authentication) {

        List<AdminApplicationResponse> applications =
                applicationRepository.findAll()
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(applications);
    }

    // GET APPLICATIONS BY STATUS
    @GetMapping("/status")
    public ResponseEntity<?> getApplicationsByStatus(
            @RequestParam String status,
            Authentication authentication) {

        List<AdminApplicationResponse> applications =
                applicationRepository
                        .findByStatus(status)
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(applications);
    }

    // UPDATE APPLICATION STATUS
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status,
            Authentication authentication) {

        Application application =
                applicationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"));

        application.setStatus(status);

        Application updated =
                applicationRepository.save(application);

        return ResponseEntity.ok(
                convertToResponse(updated)
        );
    }

    // DELETE APPLICATION
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApplication(
            @PathVariable Long id,
            Authentication authentication) {

        Application application =
                applicationRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Application not found"));

        applicationRepository.delete(application);

        return ResponseEntity.ok(
                "Application deleted successfully"
        );
    }

    // CONVERT ENTITY TO DTO
    private AdminApplicationResponse convertToResponse(
            Application application) {

        String studentName = "Unknown";
        String studentEmail = "Unknown";
        String opportunityTitle = "Unknown";
        String companyName = "Unknown";

        if (application.getStudent() != null) {
            studentName =
                    application.getStudent().getName();

            studentEmail =
                    application.getStudent().getEmail();
        }

        if (application.getOpportunity() != null) {

            opportunityTitle =
                    application.getOpportunity().getTitle();

            if (application.getOpportunity().getCompany() != null) {

                companyName =
                        application.getOpportunity()
                                .getCompany()
                                .getCompanyName();
            }
        }

        return new AdminApplicationResponse(
                application.getId(),
                studentName,
                studentEmail,
                opportunityTitle,
                companyName,
                application.getStatus(),
                application.getCoverLetter(),
                application.getResumeUrl(),
                application.getAppliedAt()
        );
    }
}