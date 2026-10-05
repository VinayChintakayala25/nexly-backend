package com.nexly.backend.controller;

import com.nexly.backend.dto.OpportunitySkillRequest;
import com.nexly.backend.entity.Company;
import com.nexly.backend.entity.Opportunity;
import com.nexly.backend.entity.OpportunitySkill;
import com.nexly.backend.entity.Skill;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.CompanyRepository;
import com.nexly.backend.repository.OpportunityRepository;
import com.nexly.backend.repository.OpportunitySkillRepository;
import com.nexly.backend.repository.SkillRepository;
import com.nexly.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter/opportunities")
@CrossOrigin(origins = "http://localhost:5173")
public class OpportunitySkillController {

    private final OpportunityRepository opportunityRepository;
    private final OpportunitySkillRepository opportunitySkillRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public OpportunitySkillController(
            OpportunityRepository opportunityRepository,
            OpportunitySkillRepository opportunitySkillRepository,
            SkillRepository skillRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository) {

        this.opportunityRepository = opportunityRepository;
        this.opportunitySkillRepository = opportunitySkillRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // RECRUITER: Add required skill to opportunity
    // =========================================================
    @PostMapping("/{opportunityId}/skills")
    public ResponseEntity<?> addSkill(
            Authentication authentication,
            @PathVariable Long opportunityId,
            @RequestBody OpportunitySkillRequest request) {

        User recruiter = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Opportunity opportunity =
                opportunityRepository.findById(opportunityId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Opportunity not found"));

        // Check ownership
        if (!opportunity.getCompany()
                .getRecruiter()
                .getId()
                .equals(recruiter.getId())) {

            return ResponseEntity.status(403)
                    .body("You cannot modify this opportunity");
        }

        if (request.getSkillName() == null ||
                request.getSkillName().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Skill name is required");
        }

        // Find existing skill or create new skill
        Skill skill = skillRepository
                .findByNameIgnoreCase(request.getSkillName())
                .orElseGet(() ->
                        skillRepository.save(
                                new Skill(request.getSkillName())
                        ));

        // Prevent duplicate skill
        if (opportunitySkillRepository
                .findByOpportunityAndSkill(
                        opportunity,
                        skill
                ).isPresent()) {

            return ResponseEntity.badRequest()
                    .body("Skill already added");
        }

        OpportunitySkill opportunitySkill =
                new OpportunitySkill();

        opportunitySkill.setOpportunity(opportunity);
        opportunitySkill.setSkill(skill);

        opportunitySkill.setLevel(
                request.getLevel() == null ||
                        request.getLevel().isBlank()
                        ? "BEGINNER"
                        : request.getLevel().toUpperCase()
        );

        return ResponseEntity.ok(
                opportunitySkillRepository.save(
                        opportunitySkill
                )
        );
    }

    // =========================================================
    // RECRUITER: View required skills
    // =========================================================
    @GetMapping("/{opportunityId}/skills")
    public ResponseEntity<List<OpportunitySkill>> getSkills(
            Authentication authentication,
            @PathVariable Long opportunityId) {

        User recruiter = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Opportunity opportunity =
                opportunityRepository.findById(opportunityId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Opportunity not found"));

        if (!opportunity.getCompany()
                .getRecruiter()
                .getId()
                .equals(recruiter.getId())) {

            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                opportunitySkillRepository
                        .findByOpportunity(opportunity)
        );
    }

    // =========================================================
    // RECRUITER: Delete required skill
    // =========================================================
    @DeleteMapping("/skills/{skillId}")
    public ResponseEntity<?> deleteSkill(
            Authentication authentication,
            @PathVariable Long skillId) {

        User recruiter = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        OpportunitySkill opportunitySkill =
                opportunitySkillRepository
                        .findById(skillId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Opportunity skill not found"));

        if (!opportunitySkill
                .getOpportunity()
                .getCompany()
                .getRecruiter()
                .getId()
                .equals(recruiter.getId())) {

            return ResponseEntity.status(403)
                    .body("You cannot delete this skill");
        }

        opportunitySkillRepository.delete(opportunitySkill);

        return ResponseEntity.ok(
                "Opportunity skill deleted successfully"
        );
    }
}