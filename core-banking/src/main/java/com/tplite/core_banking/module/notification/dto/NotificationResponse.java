package com.tplite.core_banking.module.notification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

import com.tplite.core_banking.module.notification.entity.Notification;
import com.tplite.core_banking.module.notification.entity.NotificationChannel;
import com.tplite.core_banking.module.notification.entity.NotificationStatus;
import com.tplite.core_banking.module.notification.entity.NotificationType;

@Getter
@Setter
@NoArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String title;
    private String content;
    private NotificationType notificationType;
    private NotificationChannel channel;
    private NotificationStatus status;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setTitle(notification.getTitle());
        response.setContent(notification.getContent());
        response.setNotificationType(notification.getNotificationType());
        response.setChannel(notification.getChannel());
        response.setStatus(notification.getStatus());
        response.setReadAt(notification.getReadAt());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}
