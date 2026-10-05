package com.nexly.backend.dto;

import java.time.LocalDateTime;

public class AdminApplicationResponse {

    private Long id;
    private String studentName;
    private String studentEmail;
    private String opportunityTitle;
    private String companyName;
    private String status;
    private String coverLetter;
    private String resumeUrl;
    private LocalDateTime appliedAt;

    public AdminApplicationResponse(
            Long id,
            String studentName,
            String studentEmail,
            String opportunityTitle,
            String companyName,
            String status,
            String coverLetter,
            String resumeUrl,
            LocalDateTime appliedAt) {

        this.id = id;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.opportunityTitle = opportunityTitle;
        this.companyName = companyName;
        this.status = status;
        this.coverLetter = coverLetter;
        this.resumeUrl = resumeUrl;
        this.appliedAt = appliedAt;
    }

    public Long getId() {
        return id;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getOpportunityTitle() {
        return opportunityTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getStatus() {
        return status;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public String getResumeUrl() {
        return resumeUrl;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }
}