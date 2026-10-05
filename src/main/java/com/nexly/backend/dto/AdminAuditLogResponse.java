package com.nexly.backend.dto;

import java.time.LocalDateTime;

public class AdminAuditLogResponse {

    private Long id;

    private Long userId;

    private String userName;

    private String userEmail;

    private String action;

    private String description;

    private String entityType;

    private Long entityId;

    private LocalDateTime createdAt;

    public AdminAuditLogResponse(
            Long id,
            Long userId,
            String userName,
            String userEmail,
            String action,
            String description,
            String entityType,
            Long entityId,
            LocalDateTime createdAt) {

        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.action = action;
        this.description = description;
        this.entityType = entityType;
        this.entityId = entityId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getAction() {
        return action;
    }

    public String getDescription() {
        return description;
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}