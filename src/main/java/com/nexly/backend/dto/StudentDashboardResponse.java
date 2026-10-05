package com.nexly.backend.dto;

public class StudentDashboardResponse {

    private long totalApplications;
    private long underReview;
    private long shortlisted;
    private long selected;
    private long rejected;
    private long savedOpportunities;
    private long upcomingInterviews;
    private long unreadNotifications;

    public StudentDashboardResponse() {
    }

    public StudentDashboardResponse(
            long totalApplications,
            long underReview,
            long shortlisted,
            long selected,
            long rejected,
            long savedOpportunities,
            long upcomingInterviews,
            long unreadNotifications) {

        this.totalApplications = totalApplications;
        this.underReview = underReview;
        this.shortlisted = shortlisted;
        this.selected = selected;
        this.rejected = rejected;
        this.savedOpportunities = savedOpportunities;
        this.upcomingInterviews = upcomingInterviews;
        this.unreadNotifications = unreadNotifications;
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

    public long getRejected() {
        return rejected;
    }

    public long getSavedOpportunities() {
        return savedOpportunities;
    }

    public long getUpcomingInterviews() {
        return upcomingInterviews;
    }

    public long getUnreadNotifications() {
        return unreadNotifications;
    }
}