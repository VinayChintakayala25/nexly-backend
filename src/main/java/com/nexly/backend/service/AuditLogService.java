package com.nexly.backend.service;

import com.nexly.backend.entity.AuditLog;
import com.nexly.backend.entity.User;
import com.nexly.backend.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(
            AuditLogRepository auditLogRepository) {

        this.auditLogRepository = auditLogRepository;
    }

    public void log(
            User user,
            String action,
            String description,
            String entityType,
            Long entityId) {

        AuditLog auditLog = new AuditLog();

        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setDescription(description);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);

        auditLogRepository.save(auditLog);
    }
}