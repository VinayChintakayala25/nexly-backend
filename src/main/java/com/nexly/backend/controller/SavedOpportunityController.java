package com.nexly.backend.controller;

import com.nexly.backend.entity.Opportunity;
import com.nexly.backend.entity.SavedOpportunity;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.OpportunityRepository;
import com.nexly.backend.repository.SavedOpportunityRepository;
import com.nexly.backend.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/student/saved")
@CrossOrigin(origins = "http://localhost:5173")
public class SavedOpportunityController {

    private final SavedOpportunityRepository savedRepository;
    private final OpportunityRepository opportunityRepository;
    private final UserRepository userRepository;

    public SavedOpportunityController(
            SavedOpportunityRepository savedRepository,
            OpportunityRepository opportunityRepository,
            UserRepository userRepository) {

        this.savedRepository = savedRepository;
        this.opportunityRepository = opportunityRepository;
        this.userRepository = userRepository;
    }

    // =========================================================
    // SAVE OPPORTUNITY
    // =========================================================

    @PostMapping("/{opportunityId}")
    public ResponseEntity<?> saveOpportunity(
            Authentication authentication,
            @PathVariable Long opportunityId) {

        User student = getStudent(authentication);

        Opportunity opportunity =
                opportunityRepository.findById(opportunityId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Opportunity not found"
                                ));

        if (!"PUBLISHED".equalsIgnoreCase(
                opportunity.getStatus())) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "This opportunity is not available."
                    ));
        }

        if (savedRepository.existsByStudentAndOpportunity(
                student,
                opportunity)) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Opportunity is already saved."
                    ));
        }

        SavedOpportunity saved =
                new SavedOpportunity();

        saved.setStudent(student);
        saved.setOpportunity(opportunity);

        SavedOpportunity savedOpportunity =
                savedRepository.save(saved);

        return ResponseEntity.ok(
                buildResponse(savedOpportunity)
        );
    }


    // =========================================================
    // GET ALL SAVED OPPORTUNITIES
    // =========================================================

    @GetMapping
    public ResponseEntity<?> getSavedOpportunities(
            Authentication authentication) {

        User student = getStudent(authentication);

        List<SavedOpportunity> saved =
                savedRepository
                        .findByStudentOrderBySavedAtDesc(student);

        List<Map<String, Object>> response =
                saved.stream()
                        .map(this::buildResponse)
                        .toList();

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // CHECK SAVE STATUS
    // =========================================================

    @GetMapping("/{opportunityId}/status")
    public ResponseEntity<?> getSaveStatus(
            Authentication authentication,
            @PathVariable Long opportunityId) {

        User student = getStudent(authentication);

        Opportunity opportunity =
                opportunityRepository.findById(opportunityId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Opportunity not found"
                                ));

        boolean saved =
                savedRepository
                        .existsByStudentAndOpportunity(
                                student,
                                opportunity
                        );

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put("saved", saved);

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // REMOVE SAVED OPPORTUNITY
    // =========================================================

    @DeleteMapping("/{opportunityId}")
    public ResponseEntity<?> removeSavedOpportunity(
            Authentication authentication,
            @PathVariable Long opportunityId) {

        User student = getStudent(authentication);

        Opportunity opportunity =
                opportunityRepository.findById(opportunityId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Opportunity not found"
                                ));

        SavedOpportunity saved =
                savedRepository
                        .findByStudentAndOpportunity(
                                student,
                                opportunity
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Saved opportunity not found"
                                ));

        savedRepository.delete(saved);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "message",
                "Opportunity removed from saved list."
        );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // FIND CURRENT STUDENT
    // =========================================================

    private User getStudent(
            Authentication authentication) {

        if (authentication == null) {
            throw new RuntimeException(
                    "Authentication is required."
            );
        }

        return userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found."
                        ));
    }


    // =========================================================
    // BUILD SAFE JSON RESPONSE
    // =========================================================

    private Map<String, Object> buildResponse(
            SavedOpportunity saved) {

        Opportunity opportunity =
                saved.getOpportunity();

        Map<String, Object> opportunityData =
                new LinkedHashMap<>();

        opportunityData.put(
                "id",
                opportunity.getId()
        );

        opportunityData.put(
                "title",
                opportunity.getTitle()
        );

        opportunityData.put(
                "type",
                opportunity.getType()
        );

        opportunityData.put(
                "location",
                opportunity.getLocation()
        );

        opportunityData.put(
                "workMode",
                opportunity.getWorkMode()
        );

        opportunityData.put(
                "description",
                opportunity.getDescription()
        );

        opportunityData.put(
                "requirements",
                opportunity.getRequirements()
        );

        opportunityData.put(
                "salary",
                opportunity.getSalary()
        );

        opportunityData.put(
                "applicationDeadline",
                opportunity.getApplicationDeadline()
        );

        opportunityData.put(
                "status",
                opportunity.getStatus()
        );


        // -----------------------------------------------------
        // COMPANY
        // -----------------------------------------------------

        if (opportunity.getCompany() != null) {

            Map<String, Object> companyData =
                    new LinkedHashMap<>();

            companyData.put(
                    "id",
                    opportunity.getCompany().getId()
            );

            companyData.put(
                    "companyName",
                    opportunity.getCompany().getCompanyName()
            );

            opportunityData.put(
                    "company",
                    companyData
            );

        } else {

            opportunityData.put(
                    "company",
                    null
            );
        }


        // -----------------------------------------------------
        // FINAL RESPONSE
        // -----------------------------------------------------

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "id",
                saved.getId()
        );

        response.put(
                "savedAt",
                saved.getSavedAt()
        );

        response.put(
                "opportunity",
                opportunityData
        );

        return response;
    }
}