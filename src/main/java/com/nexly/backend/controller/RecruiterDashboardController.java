package com.nexly.backend.controller;

import com.nexly.backend.dto.RecruiterDashboardResponse;
import com.nexly.backend.entity.Company;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.ApplicationRepository;
import com.nexly.backend.repository.CompanyRepository;
import com.nexly.backend.repository.InterviewRepository;
import com.nexly.backend.repository.NotificationRepository;
import com.nexly.backend.repository.OpportunityRepository;
import com.nexly.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recruiter/dashboard")
@CrossOrigin(origins = "http://localhost:5173")
public class RecruiterDashboardController {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final OpportunityRepository opportunityRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final NotificationRepository notificationRepository;

    public RecruiterDashboardController(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            OpportunityRepository opportunityRepository,
            ApplicationRepository applicationRepository,
            InterviewRepository interviewRepository,
            NotificationRepository notificationRepository) {

        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.opportunityRepository = opportunityRepository;
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public ResponseEntity<?> getDashboard(
            Authentication authentication) {

        User recruiter = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Recruiter not found"));

        Company company = companyRepository
                .findByRecruiter(recruiter)
                .orElseThrow(() ->
                        new RuntimeException("Company profile not found"));

        long totalOpportunities =
                opportunityRepository.countByCompany(company);

        long publishedOpportunities =
                opportunityRepository.countByCompanyAndStatus(
                        company,
                        "PUBLISHED"
                );

        long totalApplications =
                applicationRepository.countByOpportunityCompany(company);

        long underReview =
                applicationRepository.countByOpportunityCompanyAndStatus(
                        company,
                        "UNDER_REVIEW"
                );

        long shortlisted =
                applicationRepository.countByOpportunityCompanyAndStatus(
                        company,
                        "SHORTLISTED"
                );

        long selected =
                applicationRepository.countByOpportunityCompanyAndStatus(
                        company,
                        "SELECTED"
                );

        long interviews =
                interviewRepository
                        .findByApplicationOpportunityCompanyRecruiterId(
                                recruiter.getId()
                        )
                        .size();

        long unreadNotifications =
                notificationRepository.countByUserAndReadFalse(
                        recruiter
                );

        RecruiterDashboardResponse response =
                new RecruiterDashboardResponse(
                        totalOpportunities,
                        publishedOpportunities,
                        totalApplications,
                        underReview,
                        shortlisted,
                        selected,
                        interviews,
                        unreadNotifications
                );

        return ResponseEntity.ok(response);
    }
}