package com.nexly.backend.repository;

import com.nexly.backend.entity.Application;
import com.nexly.backend.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewRepository
        extends JpaRepository<Interview, Long> {

    Optional<Interview> findByApplication(Application application);

    List<Interview> findByApplicationStudentId(Long studentId);

    List<Interview> findByApplicationOpportunityCompanyRecruiterId(
            Long recruiterId
    );

    long countByApplicationStudentId(Long studentId);
}