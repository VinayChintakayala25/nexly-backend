package com.nexly.backend.controller;

import com.nexly.backend.entity.Opportunity;
import com.nexly.backend.repository.OpportunityRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/opportunities")
@CrossOrigin(origins = "http://localhost:5173")
public class OpportunityBrowseController {

    private final OpportunityRepository opportunityRepository;

    public OpportunityBrowseController(
            OpportunityRepository opportunityRepository) {

        this.opportunityRepository = opportunityRepository;
    }

    // =========================================================
    // GET ALL PUBLISHED OPPORTUNITIES
    // =========================================================
    @GetMapping
    public ResponseEntity<List<Opportunity>> getOpportunities(

            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String workMode) {

        List<Opportunity> opportunities;

        // Search by keyword/title
        if (keyword != null && !keyword.isBlank()) {

            opportunities =
                    opportunityRepository
                            .findByStatusAndTitleContainingIgnoreCase(
                                    "PUBLISHED",
                                    keyword
                            );

            // Filter by type
        } else if (type != null && !type.isBlank()) {

            opportunities =
                    opportunityRepository
                            .findByStatusAndTypeIgnoreCase(
                                    "PUBLISHED",
                                    type
                            );

            // Filter by location
        } else if (location != null && !location.isBlank()) {

            opportunities =
                    opportunityRepository
                            .findByStatusAndLocationContainingIgnoreCase(
                                    "PUBLISHED",
                                    location
                            );

            // Filter by work mode
        } else if (workMode != null && !workMode.isBlank()) {

            opportunities =
                    opportunityRepository
                            .findByStatusAndWorkModeIgnoreCase(
                                    "PUBLISHED",
                                    workMode
                            );

            // No filters
        } else {

            opportunities =
                    opportunityRepository
                            .findByStatus("PUBLISHED");
        }

        return ResponseEntity.ok(opportunities);
    }

    // =========================================================
    // GET ONE PUBLISHED OPPORTUNITY
    // =========================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getOpportunity(
            @PathVariable Long id) {

        Opportunity opportunity =
                opportunityRepository
                        .findById(id)
                        .orElse(null);

        if (opportunity == null) {
            return ResponseEntity.notFound().build();
        }

        if (!"PUBLISHED".equalsIgnoreCase(
                opportunity.getStatus())) {

            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(opportunity);
    }
}