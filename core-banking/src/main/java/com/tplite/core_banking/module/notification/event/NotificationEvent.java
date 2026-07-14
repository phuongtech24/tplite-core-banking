package com.tplite.core_banking.module.notification.event;

import java.util.UUID;

import com.tplite.core_banking.module.notification.entity.NotificationType;

public class NotificationEvent {
    private UUID userId;
    private String title;
    private String content;
    private NotificationType notificationType;

    public NotificationEvent() {
    }

    public NotificationEvent(UUID userId, String title, String content, NotificationType notificationType) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.notificationType = notificationType;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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
}
