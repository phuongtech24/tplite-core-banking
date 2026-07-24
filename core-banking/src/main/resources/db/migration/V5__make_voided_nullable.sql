ALTER TABLE users
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE roles
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE permissions
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE user_roles
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE role_permissions
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE refresh_tokens
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE customers
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE customer_addresses
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE kyc_documents
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE accounts
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE cards
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE transactions
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE audit_logs
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE loan_products
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE loans
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE loan_repayment_schedules
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE loan_payments
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE notifications
    ALTER COLUMN voided DROP NOT NULL;

ALTER TABLE notification_outbox_events
    ALTER COLUMN voided DROP NOT NULL;
