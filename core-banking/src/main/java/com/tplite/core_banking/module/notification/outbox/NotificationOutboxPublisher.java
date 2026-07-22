package com.tplite.core_banking.module.notification.outbox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tplite.core_banking.module.notification.event.NotificationEvent;

@Component
public class NotificationOutboxPublisher {
    private static final Logger log = LoggerFactory.getLogger(NotificationOutboxPublisher.class);

    private final NotificationOutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final int batchSize;
    private final int maxRetries;
    private final long initialBackoffSeconds;
    private final String dlqTopic;

    public NotificationOutboxPublisher(
            NotificationOutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, NotificationEvent> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${app.kafka.outbox.batch-size:20}") int batchSize,
            @Value("${app.kafka.outbox.max-retries:3}") int maxRetries,
            @Value("${app.kafka.outbox.initial-backoff-seconds:5}") long initialBackoffSeconds,
            @Value("${app.kafka.topics.notification-events-dlq:notification.events.dlq}") String dlqTopic
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.batchSize = batchSize;
        this.maxRetries = maxRetries;
        this.initialBackoffSeconds = initialBackoffSeconds;
        this.dlqTopic = dlqTopic;
    }

    @Scheduled(fixedDelayString = "${app.kafka.outbox.fixed-delay-millis:5000}")
    @Transactional
    public void publishPendingEvents() {
        List<NotificationOutboxEvent> events = outboxEventRepository
                .findByStatusAndNextRetryAtLessThanEqualOrderByCreatedAtAsc(
                        OutboxEventStatus.PENDING,
                        LocalDateTime.now(),
                        PageRequest.of(0, batchSize)
                );

        for (NotificationOutboxEvent event : events) {
            publishOne(event.getId());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void publishOne(java.util.UUID eventId) {
        NotificationOutboxEvent outboxEvent = outboxEventRepository.findById(eventId).orElse(null);
        if (outboxEvent == null || outboxEvent.getStatus() != OutboxEventStatus.PENDING) {
            return;
        }

        try {
            NotificationEvent event = objectMapper.readValue(outboxEvent.getPayload(), NotificationEvent.class);
            kafkaTemplate.send(outboxEvent.getTopic(), outboxEvent.getEventKey(), event).get(5, TimeUnit.SECONDS);

            outboxEvent.setStatus(OutboxEventStatus.PUBLISHED);
            outboxEvent.setPublishedAt(LocalDateTime.now());
            outboxEvent.setLastError(null);
            outboxEventRepository.save(outboxEvent);
            log.info("Outbox event published: eventId={}, topic={}", outboxEvent.getId(), outboxEvent.getTopic());
        } catch (Exception ex) {
            handlePublishFailure(outboxEvent, ex);
        }
    }

    private void handlePublishFailure(NotificationOutboxEvent outboxEvent, Exception ex) {
        int nextRetryCount = outboxEvent.getRetryCount() + 1;
        outboxEvent.setRetryCount(nextRetryCount);
        outboxEvent.setLastError(ex.getMessage());

        if (nextRetryCount >= maxRetries) {
            sendToDlq(outboxEvent);
            outboxEvent.setStatus(OutboxEventStatus.FAILED);
            outboxEventRepository.save(outboxEvent);
            log.error("Outbox event moved to DLQ: eventId={}, retries={}", outboxEvent.getId(), nextRetryCount, ex);
            return;
        }

        long backoffSeconds = initialBackoffSeconds * (1L << Math.max(0, nextRetryCount - 1));
        outboxEvent.setNextRetryAt(LocalDateTime.now().plusSeconds(backoffSeconds));
        outboxEventRepository.save(outboxEvent);
        log.warn("Outbox event publish failed, scheduled retry: eventId={}, retryCount={}, nextRetryAt={}",
                outboxEvent.getId(), nextRetryCount, outboxEvent.getNextRetryAt(), ex);
    }

    private void sendToDlq(NotificationOutboxEvent outboxEvent) {
        try {
            NotificationEvent event = objectMapper.readValue(outboxEvent.getPayload(), NotificationEvent.class);
            kafkaTemplate.send(dlqTopic, outboxEvent.getEventKey(), event).get(5, TimeUnit.SECONDS);
        } catch (Exception dlqEx) {
            outboxEvent.setLastError(outboxEvent.getLastError() + " | DLQ publish failed: " + dlqEx.getMessage());
            log.error("Failed to publish outbox event to DLQ: eventId={}, dlqTopic={}", outboxEvent.getId(), dlqTopic, dlqEx);
        }
    }
}
