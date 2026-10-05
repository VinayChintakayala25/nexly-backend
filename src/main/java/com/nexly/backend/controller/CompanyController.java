package com.nexly.backend.controller;

import com.nexly.backend.dto.CompanyRequest;
import com.nexly.backend.entity.Company;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.CompanyRepository;
import com.nexly.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter/company")
@CrossOrigin(origins = "http://localhost:5173")
public class CompanyController {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyController(
            CompanyRepository companyRepository,
            UserRepository userRepository) {

        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> saveCompany(
            Authentication authentication,
            @RequestBody CompanyRequest request) {

        User recruiter = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Company company = companyRepository
                .findByRecruiter(recruiter)
                .orElse(new Company());

        company.setRecruiter(recruiter);
        company.setCompanyName(request.getCompanyName());
        company.setIndustry(request.getIndustry());
        company.setWebsite(request.getWebsite());
        company.setLocation(request.getLocation());
        company.setDescription(request.getDescription());

        return ResponseEntity.ok(
                companyRepository.save(company)
        );
    }

    @GetMapping
    public ResponseEntity<?> getCompany(
            Authentication authentication) {

        User recruiter = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return companyRepository
                .findByRecruiter(recruiter)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }
}