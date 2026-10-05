package com.nexly.backend.dto;

public class AdminDashboardResponse {

    private long totalUsers;
    private long totalStudents;
    private long totalRecruiters;
    private long totalOpportunities;
    private long publishedOpportunities;
    private long totalApplications;
    private long selectedApplications;
    private long unreadNotifications;

    public AdminDashboardResponse() {
    }

    public AdminDashboardResponse(
            long totalUsers,
            long totalStudents,
            long totalRecruiters,
            long totalOpportunities,
            long publishedOpportunities,
            long totalApplications,
            long selectedApplications,
            long unreadNotifications) {

        this.totalUsers = totalUsers;
        this.totalStudents = totalStudents;
        this.totalRecruiters = totalRecruiters;
        this.totalOpportunities = totalOpportunities;
        this.publishedOpportunities = publishedOpportunities;
        this.totalApplications = totalApplications;
        this.selectedApplications = selectedApplications;
        this.unreadNotifications = unreadNotifications;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public long getTotalRecruiters() {
        return totalRecruiters;
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

    public long getSelectedApplications() {
        return selectedApplications;
    }

    public long getUnreadNotifications() {
        return unreadNotifications;
    }
}