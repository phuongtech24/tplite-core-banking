package com.tplite.core_banking.module.notification.event;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tplite.core_banking.module.notification.entity.NotificationType;
import com.tplite.core_banking.module.notification.outbox.NotificationOutboxEvent;
import com.tplite.core_banking.module.notification.outbox.NotificationOutboxEventRepository;
import com.tplite.core_banking.module.user.entity.User;

@Component
public class NotificationEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(NotificationEventPublisher.class);

    private final NotificationOutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final String notificationTopic;

    public NotificationEventPublisher(
            NotificationOutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper,
            @Value("${app.kafka.topics.notification-events}") String notificationTopic
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
        this.notificationTopic = notificationTopic;
    }

    @Transactional
    public void publishAfterCommit(User user, String title, String content, NotificationType type) {
        NotificationEvent event = new NotificationEvent(UUID.randomUUID(), user.getId(), title, content, type);
        NotificationOutboxEvent outboxEvent = new NotificationOutboxEvent();
        outboxEvent.setId(event.getEventId());
        outboxEvent.setAggregateType("USER");
        outboxEvent.setAggregateId(user.getId().toString());
        outboxEvent.setEventType("NOTIFICATION_CREATED");
        outboxEvent.setTopic(notificationTopic);
        outboxEvent.setEventKey(user.getId().toString());
        outboxEvent.setPayload(toJson(event));
        outboxEvent.setNextRetryAt(LocalDateTime.now());

        outboxEventRepository.save(outboxEvent);
        log.info("Notification outbox event saved: eventId={}, userId={}, type={}",
                event.getEventId(), event.getUserId(), event.getNotificationType());
    }

    private String toJson(NotificationEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Cannot serialize notification event", ex);
        }
    }
}
