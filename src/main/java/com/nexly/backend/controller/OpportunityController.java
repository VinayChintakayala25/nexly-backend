package com.nexly.backend.controller;

import com.nexly.backend.dto.OpportunityRequest;
import com.nexly.backend.entity.Company;
import com.nexly.backend.entity.Opportunity;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.CompanyRepository;
import com.nexly.backend.repository.OpportunityRepository;
import com.nexly.backend.repository.UserRepository;
import com.nexly.backend.service.AuditLogService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter/opportunities")
@CrossOrigin(origins = "http://localhost:5173")
public class OpportunityController {

    private final OpportunityRepository opportunityRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public OpportunityController(
            OpportunityRepository opportunityRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository,
            AuditLogService auditLogService) {

        this.opportunityRepository = opportunityRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
    }

    // =========================================================
    // RECRUITER: CREATE OPPORTUNITY
    // =========================================================
    @PostMapping
    public ResponseEntity<?> createOpportunity(
            Authentication authentication,
            @RequestBody OpportunityRequest request) {

        User recruiter = getUser(authentication);

        if (recruiter.getRole() != User.Role.RECRUITER) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Only recruiters can create opportunities");
        }

        if (request.getTitle() == null ||
                request.getTitle().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Opportunity title is required");
        }

        if (request.getType() == null ||
                request.getType().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Opportunity type is required");
        }

        if (request.getDescription() == null ||
                request.getDescription().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Opportunity description is required");
        }

        Company company = companyRepository
                .findByRecruiter(recruiter)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Create company profile first"));

        Opportunity opportunity = new Opportunity();

        opportunity.setCompany(company);

        opportunity.setTitle(
                request.getTitle().trim()
        );

        opportunity.setType(
                request.getType().trim().toUpperCase()
        );

        opportunity.setLocation(
                request.getLocation()
        );

        opportunity.setWorkMode(
                request.getWorkMode()
        );

        opportunity.setDescription(
                request.getDescription()
        );

        opportunity.setRequirements(
                request.getRequirements()
        );

        opportunity.setSalary(
                request.getSalary()
        );

        opportunity.setApplicationDeadline(
                request.getApplicationDeadline()
        );

        opportunity.setStatus("PUBLISHED");

        Opportunity savedOpportunity =
                opportunityRepository.save(opportunity);

        // =====================================================
        // AUDIT LOG
        // =====================================================

        auditLogService.log(
                recruiter,
                "OPPORTUNITY_CREATED",
                "Created opportunity: "
                        + savedOpportunity.getTitle(),
                "OPPORTUNITY",
                savedOpportunity.getId()
        );

        return ResponseEntity.ok(savedOpportunity);
    }

    // =========================================================
    // RECRUITER: VIEW MY OPPORTUNITIES
    // =========================================================
    @GetMapping
    public ResponseEntity<?> getMyOpportunities(
            Authentication authentication) {

        User recruiter = getUser(authentication);

        if (recruiter.getRole() != User.Role.RECRUITER) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Only recruiters can access opportunities");
        }

        Company company = companyRepository
                .findByRecruiter(recruiter)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Company profile not found"));

        return ResponseEntity.ok(
                opportunityRepository.findByCompany(company)
        );
    }

    // =========================================================
    // RECRUITER: VIEW PUBLISHED OPPORTUNITIES
    // =========================================================
    @GetMapping("/all")
    public ResponseEntity<List<Opportunity>> getAllOpportunities() {

        return ResponseEntity.ok(
                opportunityRepository
                        .findByStatus("PUBLISHED")
        );
    }

    // =========================================================
    // RECRUITER: DELETE OPPORTUNITY
    // =========================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOpportunity(
            Authentication authentication,
            @PathVariable Long id) {

        User recruiter = getUser(authentication);

        if (recruiter.getRole() != User.Role.RECRUITER) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Only recruiters can delete opportunities");
        }

        Company company = companyRepository
                .findByRecruiter(recruiter)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Company profile not found"));

        Opportunity opportunity =
                opportunityRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Opportunity not found"));

        // =====================================================
        // SECURITY CHECK
        // =====================================================

        if (opportunity.getCompany() == null ||
                !opportunity.getCompany()
                        .getId()
                        .equals(company.getId())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            "You cannot delete this opportunity"
                    );
        }

        // Save information before deletion
        String opportunityTitle =
                opportunity.getTitle();

        Long opportunityId =
                opportunity.getId();

        opportunityRepository.delete(opportunity);

        // =====================================================
        // AUDIT LOG
        // =====================================================

        auditLogService.log(
                recruiter,
                "OPPORTUNITY_DELETED",
                "Deleted opportunity: "
                        + opportunityTitle,
                "OPPORTUNITY",
                opportunityId
        );

        return ResponseEntity.ok(
                "Opportunity deleted successfully"
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