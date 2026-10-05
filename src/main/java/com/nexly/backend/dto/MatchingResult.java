package com.nexly.backend.dto;

import java.util.List;

public class MatchingResult {

    private Long opportunityId;
    private String opportunityTitle;

    private double matchPercentage;

    private List<String> matchingSkills;
    private List<String> missingSkills;

    public MatchingResult() {
    }

    public MatchingResult(
            Long opportunityId,
            String opportunityTitle,
            double matchPercentage,
            List<String> matchingSkills,
            List<String> missingSkills) {

        this.opportunityId = opportunityId;
        this.opportunityTitle = opportunityTitle;
        this.matchPercentage = matchPercentage;
        this.matchingSkills = matchingSkills;
        this.missingSkills = missingSkills;
    }

    public Long getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Long opportunityId) {
        this.opportunityId = opportunityId;
    }

    public String getOpportunityTitle() {
        return opportunityTitle;
    }

    public void setOpportunityTitle(String opportunityTitle) {
        this.opportunityTitle = opportunityTitle;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public List<String> getMatchingSkills() {
        return matchingSkills;
    }

    public void setMatchingSkills(List<String> matchingSkills) {
        this.matchingSkills = matchingSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }
}