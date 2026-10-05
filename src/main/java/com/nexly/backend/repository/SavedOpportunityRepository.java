package com.nexly.backend.repository;

import com.nexly.backend.entity.Opportunity;
import com.nexly.backend.entity.SavedOpportunity;
import com.nexly.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedOpportunityRepository
        extends JpaRepository<SavedOpportunity, Long> {

    List<SavedOpportunity>
    findByStudentOrderBySavedAtDesc(User student);

    Optional<SavedOpportunity>
    findByStudentAndOpportunity(
            User student,
            Opportunity opportunity
    );

    boolean existsByStudentAndOpportunity(
            User student,
            Opportunity opportunity
    );

    long countByStudent(User student);
}