package com.tplite.core_banking.module.audit.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.audit.dto.AuditLogResponse;
import com.tplite.core_banking.module.user.entity.User;

public interface AuditLogService {
    void record(User actorUser, String action, String resourceType, UUID resourceId, String description);

    PageResponse<AuditLogResponse> searchAuditLogs(String keyword, String action, String resourceType, Pageable pageable);
}
