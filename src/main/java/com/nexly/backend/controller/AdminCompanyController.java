package com.nexly.backend.controller;

import com.nexly.backend.dto.AdminCompanyResponse;
import com.nexly.backend.entity.Company;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.CompanyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/companies")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminCompanyController {

    private final CompanyRepository companyRepository;

    public AdminCompanyController(
            CompanyRepository companyRepository) {

        this.companyRepository = companyRepository;
    }

    // GET ALL COMPANIES
    @GetMapping
    public ResponseEntity<?> getAllCompanies(
            Authentication authentication) {

        List<AdminCompanyResponse> companies =
                companyRepository.findAll()
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(companies);
    }

    // GET COMPANY BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCompany(
            @PathVariable Long id,
            Authentication authentication) {

        Company company =
                companyRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Company not found"));

        return ResponseEntity.ok(
                convertToResponse(company)
        );
    }

    // DELETE COMPANY
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompany(
            @PathVariable Long id,
            Authentication authentication) {

        Company company =
                companyRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Company not found"));

        companyRepository.delete(company);

        return ResponseEntity.ok(
                "Company deleted successfully"
        );
    }

    // CONVERT ENTITY TO DTO
    private AdminCompanyResponse convertToResponse(
            Company company) {

        Long recruiterId = null;
        String recruiterName = "Unknown";
        String recruiterEmail = "Unknown";

        User recruiter = company.getRecruiter();

        if (recruiter != null) {

            recruiterId = recruiter.getId();
            recruiterName = recruiter.getName();
            recruiterEmail = recruiter.getEmail();
        }

        return new AdminCompanyResponse(
                company.getId(),
                company.getCompanyName(),
                company.getIndustry(),
                company.getWebsite(),
                company.getLocation(),
                company.getDescription(),
                recruiterId,
                recruiterName,
                recruiterEmail
        );
    }
}