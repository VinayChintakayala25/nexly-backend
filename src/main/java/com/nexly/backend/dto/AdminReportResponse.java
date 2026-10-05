package com.nexly.backend.dto;

import java.time.LocalDateTime;

public class AdminReportResponse {

    private Long id;
    private Long reportedById;
    private String reportedByName;
    private String reportedByEmail;
    private String subject;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public AdminReportResponse(
            Long id,
            Long reportedById,
            String reportedByName,
            String reportedByEmail,
            String subject,
            String description,
            String status,
            LocalDateTime createdAt,
            LocalDateTime resolvedAt) {

        this.id = id;
        this.reportedById = reportedById;
        this.reportedByName = reportedByName;
        this.reportedByEmail = reportedByEmail;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.resolvedAt = resolvedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getReportedById() {
        return reportedById;
    }

    public String getReportedByName() {
        return reportedByName;
    }

    public String getReportedByEmail() {
        return reportedByEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
}