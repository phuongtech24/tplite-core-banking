package com.tplite.core_banking.module.notification.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.tplite.core_banking.common.response.PageResponse;
import com.tplite.core_banking.module.notification.dto.NotificationResponse;
import com.tplite.core_banking.module.notification.entity.NotificationType;
import com.tplite.core_banking.module.notification.entity.NotificationStatus;
import com.tplite.core_banking.module.user.entity.User;

public interface NotificationService {
    NotificationResponse createInAppNotification(User user, String title, String content, NotificationType type);

    PageResponse<NotificationResponse> getMyNotifications(String email, NotificationStatus status, Pageable pageable);

    NotificationResponse markAsRead(String email, UUID notificationId);
}
