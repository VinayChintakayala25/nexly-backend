package com.nexly.backend.dto;

public class RecruiterDashboardResponse {

    private long totalOpportunities;
    private long publishedOpportunities;
    private long totalApplications;
    private long underReview;
    private long shortlisted;
    private long selected;
    private long interviews;
    private long unreadNotifications;

    public RecruiterDashboardResponse() {
    }

    public RecruiterDashboardResponse(
            long totalOpportunities,
            long publishedOpportunities,
            long totalApplications,
            long underReview,
            long shortlisted,
            long selected,
            long interviews,
            long unreadNotifications) {

        this.totalOpportunities = totalOpportunities;
        this.publishedOpportunities = publishedOpportunities;
        this.totalApplications = totalApplications;
        this.underReview = underReview;
        this.shortlisted = shortlisted;
        this.selected = selected;
        this.interviews = interviews;
        this.unreadNotifications = unreadNotifications;
    }

    public long getTotalOpportunities() {
        return totalOpportunities;
    }

    public long getPublishedOpportunities() {
        return publishedOpportunities;
    }

    public long getTotalApplications() {
        return totalApplications;
    }

    public long getUnderReview() {
        return underReview;
    }

    public long getShortlisted() {
        return shortlisted;
    }

    public long getSelected() {
        return selected;
    }

    public long getInterviews() {
        return interviews;
    }

    public long getUnreadNotifications() {
        return unreadNotifications;
    }
}