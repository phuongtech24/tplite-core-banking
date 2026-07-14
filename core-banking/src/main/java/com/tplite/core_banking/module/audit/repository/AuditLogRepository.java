package com.tplite.core_banking.module.audit.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tplite.core_banking.module.audit.entity.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    @Query("""
            SELECT a
            FROM AuditLog a
            WHERE (:action IS NULL OR a.action = :action)
              AND (:resourceType IS NULL OR a.resourceType = :resourceType)
              AND (
                    :keyword IS NULL
                    OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(a.action) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(a.resourceType) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(a.actorUser.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
            """)
    Page<AuditLog> searchAuditLogs(
            @Param("keyword") String keyword,
            @Param("action") String action,
            @Param("resourceType") String resourceType,
            Pageable pageable
    );
}
