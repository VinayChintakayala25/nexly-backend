package com.nexly.backend.dto;

import java.time.LocalDate;

public class AdminOpportunityResponse {

    private Long id;
    private String title;
    private String type;
    private String location;
    private String workMode;
    private String status;
    private String companyName;
    private LocalDate applicationDeadline;

    public AdminOpportunityResponse(
            Long id,
            String title,
            String type,
            String location,
            String workMode,
            String status,
            String companyName,
            LocalDate applicationDeadline) {

        this.id = id;
        this.title = title;
        this.type = type;
        this.location = location;
        this.workMode = workMode;
        this.status = status;
        this.companyName = companyName;
        this.applicationDeadline = applicationDeadline;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public String getWorkMode() {
        return workMode;
    }

    public String getStatus() {
        return status;
    }

    public String getCompanyName() {
        return companyName;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }
}