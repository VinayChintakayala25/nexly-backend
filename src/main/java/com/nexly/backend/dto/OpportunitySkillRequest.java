package com.nexly.backend.dto;

public class OpportunitySkillRequest {

    private String skillName;
    private String level;

    public OpportunitySkillRequest() {}

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}