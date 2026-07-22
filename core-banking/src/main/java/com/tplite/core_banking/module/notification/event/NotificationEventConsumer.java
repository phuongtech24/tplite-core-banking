package com.tplite.core_banking.module.notification.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.tplite.core_banking.module.notification.service.NotificationService;
import com.tplite.core_banking.module.user.entity.User;
import com.tplite.core_banking.module.user.repository.UserRepository;

@Component
public class NotificationEventConsumer {
    private static final Logger log = LoggerFactory.getLogger(NotificationEventConsumer.class);

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public NotificationEventConsumer(NotificationService notificationService, UserRepository userRepository) {
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "${app.kafka.topics.notification-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(NotificationEvent event) {
        User user = userRepository.findById(event.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Notification user not found: " + event.getUserId()));

        notificationService.createInAppNotificationFromEvent(
                event.getEventId(),
                user,
                event.getTitle(),
                event.getContent(),
                event.getNotificationType()
        );
        log.info("Notification event consumed: userId={}, type={}", event.getUserId(), event.getNotificationType());
    }
}
