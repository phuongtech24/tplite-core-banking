DROP INDEX IF EXISTS idx_notification_outbox_status_next_retry;
DROP INDEX IF EXISTS idx_notification_outbox_status_created_retry;

CREATE INDEX IF NOT EXISTS idx_notification_outbox_pending_created_retry
    ON notification_outbox_events (created_at, next_retry_at)
    WHERE status = 'PENDING';
