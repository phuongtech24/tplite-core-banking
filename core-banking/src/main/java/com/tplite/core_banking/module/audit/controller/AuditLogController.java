package com.tplite.core_banking.module.audit.controller;

import jakarta.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tplite.core_banking.common.response.ApiResponse;
import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.audit.dto.AuditLogResponse;
import com.tplite.core_banking.module.audit.service.AuditLogService;

@RestController
@Validated
@RequestMapping("/api/admin/audit-logs")
public class AuditLogController {
    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> searchAuditLogs(
            @Size(max = 100, message = "Keyword must not exceed 100 characters") @RequestParam(required = false) String keyword,
            @Size(max = 50, message = "Action must not exceed 50 characters") @RequestParam(required = false) String action,
            @Size(max = 50, message = "Resource type must not exceed 50 characters") @RequestParam(required = false) String resourceType,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        PageResponse<AuditLogResponse> response = auditLogService.searchAuditLogs(keyword, action, resourceType, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search audit logs success", response));
    }
}
