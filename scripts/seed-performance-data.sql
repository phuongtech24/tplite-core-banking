-- Performance seed data for local benchmark only.
-- Target size:
--   accounts: 10,000 rows
--   transactions: 50,000 rows
--   notification_outbox_events: 30,000 rows
--
-- This script resets and reseeds fixed benchmark rows.

BEGIN;

DELETE FROM notification_outbox_events
WHERE id >= 'cccccccc-cccc-cccc-cccc-000000000001'::uuid
  AND id <= 'cccccccc-cccc-cccc-cccc-000000030000'::uuid;

DELETE FROM transactions
WHERE id >= 'bbbbbbbb-bbbb-bbbb-bbbb-000000000001'::uuid
  AND id <= 'bbbbbbbb-bbbb-bbbb-bbbb-000000050000'::uuid;

DELETE FROM accounts
WHERE id >= 'aaaaaaaa-aaaa-aaaa-aaaa-000000000001'::uuid
  AND id <= 'aaaaaaaa-aaaa-aaaa-aaaa-000000010000'::uuid;

DELETE FROM customers
WHERE id = '22222222-2222-2222-2222-222222222222'::uuid;

DELETE FROM users
WHERE id = '11111111-1111-1111-1111-111111111111'::uuid;

INSERT INTO users (id, email, password, full_name, status, created_at, updated_at, voided)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'perf.customer@tplite.vn',
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiXa3euGEk9NID7QL6VSA7xR3klWXPC',
    'Performance Customer',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    FALSE
);

INSERT INTO customers (
    id,
    user_id,
    customer_code,
    full_name,
    date_of_birth,
    gender,
    phone,
    email,
    status,
    created_at,
    updated_at,
    voided
)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    'PERF-CUS-000001',
    'Performance Customer',
    DATE '2000-01-01',
    'MALE',
    '0900000000',
    'perf.customer@tplite.vn',
    'ACTIVE',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    FALSE
)
ON CONFLICT (customer_code) DO NOTHING;

INSERT INTO accounts (
    id,
    user_id,
    customer_id,
    account_number,
    account_type,
    currency,
    balance,
    frozen_amount,
    status,
    opened_at,
    version,
    created_at,
    updated_at,
    voided
)
SELECT
    format('aaaaaaaa-aaaa-aaaa-aaaa-%012s', lpad(gs::text, 12, '0'))::uuid,
    '11111111-1111-1111-1111-111111111111'::uuid,
    '22222222-2222-2222-2222-222222222222'::uuid,
    '988' || lpad(gs::text, 17, '0'),
    CASE WHEN gs % 2 = 0 THEN 'SAVING' ELSE 'PAYMENT' END,
    'VND',
    100000000.00,
    0.00,
    CASE WHEN gs % 10 = 0 THEN 'LOCKED' ELSE 'ACTIVE' END,
    CURRENT_TIMESTAMP - (gs || ' minutes')::interval,
    0,
    CURRENT_TIMESTAMP - (gs || ' minutes')::interval,
    CURRENT_TIMESTAMP,
    FALSE
FROM generate_series(1, 10000) AS gs;

INSERT INTO transactions (
    id,
    transaction_code,
    idempotency_key,
    from_account_id,
    to_account_id,
    amount,
    type,
    currency,
    status,
    description,
    created_by,
    created_at,
    updated_at,
    voided
)
SELECT
    format('bbbbbbbb-bbbb-bbbb-bbbb-%012s', lpad(gs::text, 12, '0'))::uuid,
    'PERF-TXN-' || lpad(gs::text, 8, '0'),
    'perf-idem-' || lpad(gs::text, 8, '0'),
    format('aaaaaaaa-aaaa-aaaa-aaaa-%012s', lpad(((gs % 10000) + 1)::text, 12, '0'))::uuid,
    format('aaaaaaaa-aaaa-aaaa-aaaa-%012s', lpad((((gs + 17) % 10000) + 1)::text, 12, '0'))::uuid,
    (10000 + (gs % 500000))::numeric(19,4),
    'TRANSFER',
    'VND',
    'SUCCESS',
    'Performance transfer ' || gs,
    '11111111-1111-1111-1111-111111111111'::uuid,
    CURRENT_TIMESTAMP - (gs || ' seconds')::interval,
    CURRENT_TIMESTAMP,
    FALSE
FROM generate_series(1, 50000) AS gs;

INSERT INTO notification_outbox_events (
    id,
    aggregate_type,
    aggregate_id,
    event_type,
    topic,
    event_key,
    payload,
    status,
    retry_count,
    next_retry_at,
    published_at,
    last_error,
    created_at,
    updated_at,
    voided
)
SELECT
    format('cccccccc-cccc-cccc-cccc-%012s', lpad(gs::text, 12, '0'))::uuid,
    'User',
    '11111111-1111-1111-1111-111111111111',
    'NOTIFICATION_CREATED',
    'notification.events',
    '11111111-1111-1111-1111-111111111111',
    '{"title":"Performance notification","content":"Seeded outbox event","type":"SYSTEM"}',
    CASE WHEN gs % 5 = 0 THEN 'FAILED' ELSE 'PENDING' END,
    CASE WHEN gs % 5 = 0 THEN 1 ELSE 0 END,
    CURRENT_TIMESTAMP - (gs || ' seconds')::interval,
    NULL,
    NULL,
    CURRENT_TIMESTAMP - (gs || ' seconds')::interval,
    CURRENT_TIMESTAMP,
    FALSE
FROM generate_series(1, 30000) AS gs;

COMMIT;

ANALYZE accounts;
ANALYZE transactions;
ANALYZE notification_outbox_events;
