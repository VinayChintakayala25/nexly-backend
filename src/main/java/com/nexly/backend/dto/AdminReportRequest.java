package com.nexly.backend.dto;

public class AdminReportRequest {

    private String subject;
    private String description;

    public AdminReportRequest() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}