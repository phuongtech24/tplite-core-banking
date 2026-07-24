CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY,
    user_id UUID UNIQUE,
    customer_code VARCHAR(30) UNIQUE,
    full_name VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(20),
    phone VARCHAR(30),
    email VARCHAR(255),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    voided BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_customers_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS customer_addresses (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    address_type VARCHAR(20),
    line1 VARCHAR(255),
    line2 VARCHAR(255),
    ward VARCHAR(255),
    district VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    voided BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_customer_addresses_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS kyc_documents (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    document_type VARCHAR(30) NOT NULL,
    document_number VARCHAR(50) NOT NULL,
    issued_date DATE,
    expired_date DATE,
    issued_by VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    voided BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_kyc_documents_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS accounts (
    id UUID PRIMARY KEY,
    user_id UUID,
    customer_id UUID,
    account_number VARCHAR(30) UNIQUE,
    account_type VARCHAR(20),
    currency VARCHAR(3) NOT NULL,
    balance NUMERIC(19,2) NOT NULL DEFAULT 0,
    frozen_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    opened_at TIMESTAMP,
    closed_at TIMESTAMP,
    version BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    voided BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_accounts_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_accounts_customer FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE TABLE IF NOT EXISTS cards (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    account_id UUID NOT NULL,
    card_number_masked VARCHAR(30) NOT NULL,
    card_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    daily_limit NUMERIC(19,2),
    issued_at TIMESTAMP,
    expired_at DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    voided BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_cards_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_cards_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);

CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY,
    transaction_code VARCHAR(50) UNIQUE,
    idempotency_key VARCHAR(80) UNIQUE,
    from_account_id UUID,
    to_account_id UUID,
    amount NUMERIC(19,4) NOT NULL,
    type VARCHAR(30) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    created_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    voided BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_transactions_from_account FOREIGN KEY (from_account_id) REFERENCES accounts(id),
    CONSTRAINT fk_transactions_to_account FOREIGN KEY (to_account_id) REFERENCES accounts(id),
    CONSTRAINT fk_transactions_created_by FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY,
    actor_user_id UUID,
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100),
    resource_id UUID,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    voided BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_audit_logs_actor_user FOREIGN KEY (actor_user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS loan_products (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    interest_rate NUMERIC(5,2) NOT NULL,
    min_amount NUMERIC(19,2),
    max_amount NUMERIC(19,2),
    min_term_months INTEGER,
    max_term_months INTEGER,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    voided BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS loans (
    id UUID PRIMARY KEY,
    loan_code VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    loan_product_id UUID NOT NULL,
    principal_amount NUMERIC(19,2) NOT NULL,
    interest_rate NUMERIC(5,2) NOT NULL,
    term_months INTEGER NOT NULL,
    outstanding_balance NUMERIC(19,2),
    status VARCHAR(30) NOT NULL,
    approved_by UUID,
    approved_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    voided BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_loans_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_loans_product FOREIGN KEY (loan_product_id) REFERENCES loan_products(id),
    CONSTRAINT fk_loans_approved_by FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS loan_repayment_schedules (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL,
    period_no INTEGER NOT NULL,
    due_date DATE NOT NULL,
    principal_due NUMERIC(19,2) NOT NULL,
    interest_due NUMERIC(19,2) NOT NULL,
    total_due NUMERIC(19,2) NOT NULL,
    paid_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    voided BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_loan_repayment_schedules_loan FOREIGN KEY (loan_id) REFERENCES loans(id)
);

CREATE TABLE IF NOT EXISTS loan_payments (
    id UUID PRIMARY KEY,
    loan_id UUID NOT NULL,
    schedule_id UUID,
    account_id UUID NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    transaction_id UUID,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    voided BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_loan_payments_loan FOREIGN KEY (loan_id) REFERENCES loans(id),
    CONSTRAINT fk_loan_payments_schedule FOREIGN KEY (schedule_id) REFERENCES loan_repayment_schedules(id),
    CONSTRAINT fk_loan_payments_account FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT fk_loan_payments_transaction FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);

CREATE INDEX IF NOT EXISTS idx_customers_status_created_at
    ON customers (status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_customers_full_name
    ON customers (full_name);

CREATE INDEX IF NOT EXISTS idx_customer_addresses_customer_id
    ON customer_addresses (customer_id);

CREATE INDEX IF NOT EXISTS idx_kyc_documents_customer_created_at
    ON kyc_documents (customer_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_kyc_documents_status_created_at
    ON kyc_documents (status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_accounts_user_created_at
    ON accounts (user_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_accounts_user_status_created_at
    ON accounts (user_id, status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_cards_customer_created_at
    ON cards (customer_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_cards_customer_status_created_at
    ON cards (customer_id, status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_transactions_from_account_created_at
    ON transactions (from_account_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_transactions_to_account_created_at
    ON transactions (to_account_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_transactions_created_by_created_at
    ON transactions (created_by, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_audit_logs_action_resource_created_at
    ON audit_logs (action, resource_type, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_loan_products_status_created_at
    ON loan_products (status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_loans_customer_created_at
    ON loans (customer_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_loans_status_created_at
    ON loans (status, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_loan_repayment_schedules_loan_due_date
    ON loan_repayment_schedules (loan_id, due_date);

CREATE INDEX IF NOT EXISTS idx_loan_payments_loan_payment_date
    ON loan_payments (loan_id, payment_date DESC);
