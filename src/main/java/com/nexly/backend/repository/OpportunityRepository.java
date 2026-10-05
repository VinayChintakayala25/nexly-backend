package com.nexly.backend.repository;

import com.nexly.backend.entity.Company;
import com.nexly.backend.entity.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OpportunityRepository
        extends JpaRepository<Opportunity, Long> {

    List<Opportunity> findByCompany(Company company);

    List<Opportunity> findByStatus(String status);

    long countByCompany(Company company);

    long countByCompanyAndStatus(
            Company company,
            String status
    );

    long countByStatus(String status);

    // Search published opportunities
    List<Opportunity> findByStatusAndTitleContainingIgnoreCase(
            String status,
            String title
    );

    // Filter by type
    List<Opportunity> findByStatusAndTypeIgnoreCase(
            String status,
            String type
    );

    // Filter by location
    List<Opportunity> findByStatusAndLocationContainingIgnoreCase(
            String status,
            String location
    );

    // Filter by work mode
    List<Opportunity> findByStatusAndWorkModeIgnoreCase(
            String status,
            String workMode
    );
}