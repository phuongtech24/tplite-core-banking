package com.tplite.core_banking.module.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.tplite.core_banking.module.notification.entity.Notification;
import com.tplite.core_banking.module.notification.entity.NotificationChannel;
import com.tplite.core_banking.module.notification.entity.NotificationStatus;
import com.tplite.core_banking.module.notification.entity.NotificationType;

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
