package com.nexly.backend.dto;

public class AdminCompanyResponse {

    private Long id;
    private String companyName;
    private String industry;
    private String website;
    private String location;
    private String description;

    private Long recruiterId;
    private String recruiterName;
    private String recruiterEmail;

    public AdminCompanyResponse(
            Long id,
            String companyName,
            String industry,
            String website,
            String location,
            String description,
            Long recruiterId,
            String recruiterName,
            String recruiterEmail) {

        this.id = id;
        this.companyName = companyName;
        this.industry = industry;
        this.website = website;
        this.location = location;
        this.description = description;
        this.recruiterId = recruiterId;
        this.recruiterName = recruiterName;
        this.recruiterEmail = recruiterEmail;
    }

    public Long getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getIndustry() {
        return industry;
    }

    public String getWebsite() {
        return website;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public Long getRecruiterId() {
        return recruiterId;
    }

    public String getRecruiterName() {
        return recruiterName;
    }

    public String getRecruiterEmail() {
        return recruiterEmail;
    }
}