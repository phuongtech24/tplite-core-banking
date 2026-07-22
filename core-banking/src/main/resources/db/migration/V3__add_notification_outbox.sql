CREATE TABLE IF NOT EXISTS notification_outbox_events (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(100),
    event_type VARCHAR(100) NOT NULL,
    topic VARCHAR(150) NOT NULL,
    event_key VARCHAR(100) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(30) NOT NULL,
    retry_count INTEGER NOT NULL DEFAULT 0,
    next_retry_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP,
    last_error TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    voided BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_notification_outbox_status_next_retry
    ON notification_outbox_events (status, next_retry_at, created_at);

ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS event_id UUID;

CREATE UNIQUE INDEX IF NOT EXISTS uk_notifications_event_id
    ON notifications (event_id)
    WHERE event_id IS NOT NULL;
