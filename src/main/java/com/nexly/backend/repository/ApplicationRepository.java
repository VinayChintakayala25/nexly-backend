package com.nexly.backend.repository;

import com.nexly.backend.entity.Application;
import com.nexly.backend.entity.Company;
import com.nexly.backend.entity.Opportunity;
import com.nexly.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    Optional<Application> findByStudentAndOpportunity(
            User student,
            Opportunity opportunity
    );

    List<Application> findByStudent(User student);

    List<Application> findByOpportunity(Opportunity opportunity);

    List<Application> findByStatus(String status);

    long countByStudent(User student);

    long countByStudentAndStatus(
            User student,
            String status
    );

    long countByOpportunityCompany(Company company);

    long countByOpportunityCompanyAndStatus(
            Company company,
            String status
    );

    long countByStatus(String status);
}