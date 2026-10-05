package com.nexly.backend.repository;

import com.nexly.backend.entity.AuditLog;
import com.nexly.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByOrderByCreatedAtDesc();

    List<AuditLog> findByUserOrderByCreatedAtDesc(
            User user
    );

    List<AuditLog> findByActionOrderByCreatedAtDesc(
            String action
    );
}