package com.nexly.backend.repository;

import com.nexly.backend.entity.Company;
import com.nexly.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository
        extends JpaRepository<Company, Long> {

    Optional<Company> findByRecruiter(User recruiter);

    boolean existsByRecruiter(User recruiter);
}