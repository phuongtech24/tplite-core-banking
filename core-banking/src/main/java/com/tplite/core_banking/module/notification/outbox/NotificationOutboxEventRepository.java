package com.tplite.core_banking.module.notification.outbox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationOutboxEventRepository extends JpaRepository<NotificationOutboxEvent, UUID> {
    List<NotificationOutboxEvent> findByStatusAndNextRetryAtLessThanEqualOrderByCreatedAtAsc(
            OutboxEventStatus status,
            LocalDateTime nextRetryAt,
            Pageable pageable
    );
}
