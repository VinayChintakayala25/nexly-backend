package com.nexly.backend.service;

import com.nexly.backend.dto.MatchingResult;
import com.nexly.backend.entity.Opportunity;
import com.nexly.backend.entity.OpportunitySkill;
import com.nexly.backend.entity.StudentSkill;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.OpportunityRepository;
import com.nexly.backend.repository.OpportunitySkillRepository;
import com.nexly.backend.repository.StudentSkillRepository;
import com.nexly.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class MatchingService {

    private final OpportunityRepository opportunityRepository;
    private final OpportunitySkillRepository opportunitySkillRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final UserRepository userRepository;

    public MatchingService(
            OpportunityRepository opportunityRepository,
            OpportunitySkillRepository opportunitySkillRepository,
            StudentSkillRepository studentSkillRepository,
            UserRepository userRepository
    ) {
        this.opportunityRepository = opportunityRepository;
        this.opportunitySkillRepository = opportunitySkillRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.userRepository = userRepository;
    }


    // =========================================================
    // CALCULATE MATCH FOR ONE OPPORTUNITY
    // =========================================================

    public MatchingResult calculateMatch(
            User student,
            Opportunity opportunity
    ) {

        List<OpportunitySkill> requiredSkills =
                opportunitySkillRepository
                        .findByOpportunity(opportunity);

        List<StudentSkill> studentSkills =
                studentSkillRepository
                        .findByStudent(student);

        List<String> matchingSkills =
                new ArrayList<>();

        List<String> missingSkills =
                new ArrayList<>();


        // -----------------------------------------------------
        // Compare opportunity skills with student skills
        // -----------------------------------------------------

        for (OpportunitySkill requiredSkill : requiredSkills) {

            if (requiredSkill.getSkill() == null) {
                continue;
            }

            String requiredSkillName =
                    requiredSkill.getSkill().getName();

            if (requiredSkillName == null ||
                    requiredSkillName.isBlank()) {
                continue;
            }

            boolean found = false;


            for (StudentSkill studentSkill : studentSkills) {

                if (studentSkill.getSkill() == null) {
                    continue;
                }

                String studentSkillName =
                        studentSkill.getSkill().getName();

                if (studentSkillName == null) {
                    continue;
                }


                if (requiredSkillName
                        .trim()
                        .equalsIgnoreCase(
                                studentSkillName.trim()
                        )) {

                    found = true;
                    break;
                }
            }


            if (found) {

                matchingSkills.add(
                        requiredSkillName
                );

            } else {

                missingSkills.add(
                        requiredSkillName
                );
            }
        }


        // =====================================================
        // MATCH PERCENTAGE
        // =====================================================

        double matchPercentage = 0;


        if (!requiredSkills.isEmpty()) {

            int validRequiredSkills =
                    matchingSkills.size()
                            + missingSkills.size();

            if (validRequiredSkills > 0) {

                matchPercentage =
                        ((double) matchingSkills.size()
                                / validRequiredSkills)
                                * 100;
            }
        }


        // Keep two decimal places

        matchPercentage =
                Math.round(
                        matchPercentage * 100.0
                ) / 100.0;


        return new MatchingResult(
                opportunity.getId(),
                opportunity.getTitle(),
                matchPercentage,
                matchingSkills,
                missingSkills
        );
    }


    // =========================================================
    // CALCULATE MATCH BY IDs
    // =========================================================

    public MatchingResult calculateMatchByIds(
            String email,
            Long opportunityId
    ) {

        User student =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );


        Opportunity opportunity =
                opportunityRepository
                        .findById(opportunityId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Opportunity not found"
                                )
                        );


        return calculateMatch(
                student,
                opportunity
        );
    }


    // =========================================================
    // GET MATCHES FOR ALL OPPORTUNITIES
    // =========================================================

    public List<MatchingResult> getMatchesForStudent(
            String email
    ) {

        User student =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Student not found"
                                )
                        );


        List<Opportunity> opportunities =
                opportunityRepository.findAll();


        List<MatchingResult> results =
                new ArrayList<>();


        for (Opportunity opportunity : opportunities) {

            MatchingResult result =
                    calculateMatch(
                            student,
                            opportunity
                    );


            results.add(result);
        }


        // Highest match first

        results.sort(
                Comparator.comparing(
                        MatchingResult::getMatchPercentage
                ).reversed()
        );


        return results;
    }
}