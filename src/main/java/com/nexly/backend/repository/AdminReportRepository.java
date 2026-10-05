package com.nexly.backend.repository;

import com.nexly.backend.entity.AdminReport;
import com.nexly.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminReportRepository
        extends JpaRepository<AdminReport, Long> {

    List<AdminReport> findByReportedByOrderByCreatedAtDesc(
            User reportedBy
    );

    List<AdminReport> findByStatusOrderByCreatedAtDesc(
            String status
    );

    long countByStatus(String status);
}