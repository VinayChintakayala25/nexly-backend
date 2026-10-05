package com.nexly.backend.controller;

import com.nexly.backend.dto.AdminAuditLogResponse;
import com.nexly.backend.entity.AuditLog;
import com.nexly.backend.repository.AuditLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminAuditLogController {

    private final AuditLogRepository auditLogRepository;

    public AdminAuditLogController(
            AuditLogRepository auditLogRepository) {

        this.auditLogRepository = auditLogRepository;
    }

    // GET ALL AUDIT LOGS
    @GetMapping
    public ResponseEntity<?> getAllLogs(
            Authentication authentication) {

        List<AdminAuditLogResponse> logs =
                auditLogRepository
                        .findAllByOrderByCreatedAtDesc()
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(logs);
    }

    // GET AUDIT LOGS BY ACTION
    @GetMapping("/action")
    public ResponseEntity<?> getLogsByAction(
            @RequestParam String action,
            Authentication authentication) {

        List<AdminAuditLogResponse> logs =
                auditLogRepository
                        .findByActionOrderByCreatedAtDesc(action)
                        .stream()
                        .map(this::convertToResponse)
                        .toList();

        return ResponseEntity.ok(logs);
    }

    // DELETE AUDIT LOG
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLog(
            @PathVariable Long id,
            Authentication authentication) {

        AuditLog log =
                auditLogRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Audit log not found"));

        auditLogRepository.delete(log);

        return ResponseEntity.ok(
                "Audit log deleted successfully"
        );
    }

    private AdminAuditLogResponse convertToResponse(
            AuditLog log) {

        Long userId = null;
        String userName = "System";
        String userEmail = "System";

        if (log.getUser() != null) {
            userId = log.getUser().getId();
            userName = log.getUser().getName();
            userEmail = log.getUser().getEmail();
        }

        return new AdminAuditLogResponse(
                log.getId(),
                userId,
                userName,
                userEmail,
                log.getAction(),
                log.getDescription(),
                log.getEntityType(),
                log.getEntityId(),
                log.getCreatedAt()
        );
    }
}