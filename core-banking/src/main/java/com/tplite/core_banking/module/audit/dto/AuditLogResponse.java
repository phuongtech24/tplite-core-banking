package com.tplite.core_banking.module.audit.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tplite.core_banking.module.audit.entity.AuditLog;

public class AuditLogResponse {
    private UUID id;
    private UUID actorUserId;
    private String actorEmail;
    private String action;
    private String resourceType;
    private UUID resourceId;
    private String ipAddress;
    private String userAgent;
    private String description;
    private LocalDateTime createdAt;

    public static AuditLogResponse from(AuditLog auditLog) {
        AuditLogResponse response = new AuditLogResponse();
        response.setId(auditLog.getId());
        response.setActorUserId(auditLog.getActorUser() == null ? null : auditLog.getActorUser().getId());
        response.setActorEmail(auditLog.getActorUser() == null ? null : auditLog.getActorUser().getEmail());
        response.setAction(auditLog.getAction());
        response.setResourceType(auditLog.getResourceType());
        response.setResourceId(auditLog.getResourceId());
        response.setIpAddress(auditLog.getIpAddress());
        response.setUserAgent(auditLog.getUserAgent());
        response.setDescription(auditLog.getDescription());
        response.setCreatedAt(auditLog.getCreatedAt());
        return response;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getActorUserId() { return actorUserId; }
    public void setActorUserId(UUID actorUserId) { this.actorUserId = actorUserId; }
    public String getActorEmail() { return actorEmail; }
    public void setActorEmail(String actorEmail) { this.actorEmail = actorEmail; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public UUID getResourceId() { return resourceId; }
    public void setResourceId(UUID resourceId) { this.resourceId = resourceId; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
