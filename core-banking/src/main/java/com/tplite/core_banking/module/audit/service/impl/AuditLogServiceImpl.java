package com.tplite.core_banking.module.audit.service.impl;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.audit.dto.AuditLogResponse;
import com.tplite.core_banking.module.audit.entity.AuditLog;
import com.tplite.core_banking.module.audit.repository.AuditLogRepository;
import com.tplite.core_banking.module.audit.service.AuditLogService;
import com.tplite.core_banking.module.user.entity.User;

@Service
public class AuditLogServiceImpl implements AuditLogService {
    private static final Logger log = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    private final AuditLogRepository auditLogRepository;

    @Autowired
    @Lazy
    private AuditLogService selfProxy;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void record(User actorUser, String action, String resourceType, UUID resourceId, String description) {
        try {
            selfProxy.recordInNewTransaction(actorUser, action, resourceType, resourceId, description);
        } catch (Exception ex) {
            log.warn("Audit log recording failed and was skipped: action={}, resourceType={}, resourceId={}, reason={}",
                    action, resourceType, resourceId, ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordInNewTransaction(User actorUser, String action, String resourceType, UUID resourceId, String description) {
        AuditLog auditLog = new AuditLog();
        auditLog.setActorUser(actorUser);
        auditLog.setAction(action);
        auditLog.setResourceType(resourceType);
        auditLog.setResourceId(resourceId);
        auditLog.setDescription(description);
        AuditLog savedAuditLog = auditLogRepository.saveAndFlush(auditLog);
        log.info("Audit log recorded: auditId={}, action={}, resourceType={}, resourceId={}",
                savedAuditLog.getId(), action, resourceType, resourceId);
    }

    @Override
    public PageResponse<AuditLogResponse> searchAuditLogs(String keyword, String action, String resourceType, Pageable pageable) {
        Page<AuditLogResponse> auditLogs = auditLogRepository.searchAuditLogs(
                        normalizeKeyword(keyword),
                        normalizeKeyword(action),
                        normalizeKeyword(resourceType),
                        pageable
                )
                .map(AuditLogResponse::from);
        return PageResponse.from(auditLogs);
    }

    private String normalizeKeyword(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
