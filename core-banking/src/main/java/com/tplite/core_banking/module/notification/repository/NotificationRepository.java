package com.tplite.core_banking.module.notification.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tplite.core_banking.module.notification.entity.Notification;
import com.tplite.core_banking.module.notification.entity.NotificationStatus;
import com.tplite.core_banking.module.user.entity.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUser(User user, Pageable pageable);

    Page<Notification> findByUserAndStatus(User user, NotificationStatus status, Pageable pageable);

    Optional<Notification> findByIdAndUser(UUID id, User user);

    boolean existsByEventId(UUID eventId);
}
