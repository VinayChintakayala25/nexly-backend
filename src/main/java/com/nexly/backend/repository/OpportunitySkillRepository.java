package com.nexly.backend.repository;

import com.nexly.backend.entity.Opportunity;
import com.nexly.backend.entity.OpportunitySkill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OpportunitySkillRepository
        extends JpaRepository<OpportunitySkill, Long> {

    List<OpportunitySkill> findByOpportunity(Opportunity opportunity);

    Optional<OpportunitySkill> findByOpportunityAndSkill(
            Opportunity opportunity,
            com.nexly.backend.entity.Skill skill
    );

    void deleteByOpportunity(Opportunity opportunity);
}