package com.tplite.core_banking.module.notification.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.tplite.core_banking.module.notification.entity.NotificationType;
import com.tplite.core_banking.module.user.entity.User;

@Component
public class NotificationEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(NotificationEventPublisher.class);

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final String notificationTopic;

    public NotificationEventPublisher(
            KafkaTemplate<String, NotificationEvent> kafkaTemplate,
            @Value("${app.kafka.topics.notification-events}") String notificationTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.notificationTopic = notificationTopic;
    }

    public void publishAfterCommit(User user, String title, String content, NotificationType type) {
        NotificationEvent event = new NotificationEvent(user.getId(), title, content, type);

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publish(event);
                }
            });
            return;
        }

        publish(event);
    }

    private void publish(NotificationEvent event) {
        kafkaTemplate.send(notificationTopic, event.getUserId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish notification event: userId={}, type={}",
                                event.getUserId(), event.getNotificationType(), ex);
                        return;
                    }
                    log.info("Notification event published: topic={}, userId={}, type={}",
                            notificationTopic, event.getUserId(), event.getNotificationType());
                });
    }
}
