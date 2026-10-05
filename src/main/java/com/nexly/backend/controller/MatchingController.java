package com.nexly.backend.controller;

import com.nexly.backend.dto.MatchingResult;
import com.nexly.backend.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/matching")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }


    // =========================================================
    // GET MATCH FOR ONE OPPORTUNITY
    // =========================================================

    @GetMapping("/{opportunityId}")
    public ResponseEntity<MatchingResult> getMatch(
            @PathVariable Long opportunityId,
            Authentication authentication
    ) {

        String email =
                authentication.getName();

        MatchingResult result =
                matchingService.calculateMatchByIds(
                        email,
                        opportunityId
                );

        return ResponseEntity.ok(result);
    }


    // =========================================================
    // GET ALL OPPORTUNITY MATCHES FOR STUDENT
    // =========================================================

    @GetMapping
    public ResponseEntity<List<MatchingResult>> getAllMatches(
            Authentication authentication
    ) {

        String email =
                authentication.getName();

        List<MatchingResult> results =
                matchingService.getMatchesForStudent(
                        email
                );

        return ResponseEntity.ok(results);
    }
}