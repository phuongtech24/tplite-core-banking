package com.tplite.core_banking.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.tplite.core_banking.module.notification.entity.NotificationType;
import com.tplite.core_banking.module.notification.event.NotificationEventPublisher;
import com.tplite.core_banking.module.notification.outbox.NotificationOutboxEvent;
import com.tplite.core_banking.module.notification.outbox.NotificationOutboxEventRepository;
import com.tplite.core_banking.module.notification.outbox.NotificationOutboxPublisher;
import com.tplite.core_banking.module.notification.outbox.OutboxEventStatus;
import com.tplite.core_banking.module.user.entity.User;
import com.tplite.core_banking.module.user.repository.UserRepository;

@SpringBootTest
@Testcontainers
class CoreBankingInfrastructureIT {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
            .withDatabaseName("tplite_core_banking_it")
            .withUsername("tplite")
            .withPassword("tplite");

    @Container
    static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("app.seed.enabled", () -> "true");
        registry.add("app.kafka.outbox.fixed-delay-millis", () -> "60000");
        registry.add("app.kafka.outbox.max-retries", () -> "3");
        registry.add("app.kafka.outbox.initial-backoff-seconds", () -> "1");
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationEventPublisher notificationEventPublisher;

    @Autowired
    private NotificationOutboxPublisher notificationOutboxPublisher;

    @Autowired
    private NotificationOutboxEventRepository outboxEventRepository;

    @Test
    void shouldStartWithRealPostgreSQLAndKafkaContainers() {
        assertThat(POSTGRES.isRunning()).isTrue();
        assertThat(KAFKA.isRunning()).isTrue();
        assertThat(userRepository.findByEmail("customer@tplite.vn")).isPresent();
    }

    @Test
    void shouldPersistOutboxEventThenPublishToRealKafka() throws InterruptedException {
        User customer = userRepository.findByEmail("customer@tplite.vn").orElseThrow();

        notificationEventPublisher.publishAfterCommit(
                customer,
                "Integration test notification",
                "Published through Testcontainers Kafka",
                NotificationType.SYSTEM
        );

        NotificationOutboxEvent pendingEvent = outboxEventRepository.findAll()
                .stream()
                .filter(event -> event.getPayload().contains("Integration test notification"))
                .findFirst()
                .orElseThrow();

        assertThat(pendingEvent.getStatus()).isEqualTo(OutboxEventStatus.PENDING);

        notificationOutboxPublisher.publishPendingEvents();

        awaitPublished(pendingEvent);
    }

    private void awaitPublished(NotificationOutboxEvent pendingEvent) throws InterruptedException {
        long deadline = System.nanoTime() + Duration.ofSeconds(10).toNanos();
        while (System.nanoTime() < deadline) {
            NotificationOutboxEvent reloaded = outboxEventRepository.findById(pendingEvent.getId()).orElseThrow();
            if (reloaded.getStatus() == OutboxEventStatus.PUBLISHED) {
                assertThat(reloaded.getPublishedAt()).isNotNull();
                return;
            }
            Thread.sleep(200);
        }

        NotificationOutboxEvent reloaded = outboxEventRepository.findById(pendingEvent.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(OutboxEventStatus.PUBLISHED);
    }
}
