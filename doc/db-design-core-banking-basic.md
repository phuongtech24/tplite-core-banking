# DB Design Core Banking Basic

File nay la thiet ke database muc co ban cho project `core-banking`.

Muc tieu:

- Du de hoc nghiep vu va lam API Spring Boot.
- Khong phuc tap nhu core banking that.
- Uu tien ro module, ro quan he, de code Auth/JWT/RBAC, Account, Transfer, Loan, Notification.

## 1. Tong quan module

```text
Auth/RBAC
Customer/KYC
Account
Transaction/Transfer
Loan
Card
Notification
Audit Log
```

Quan he lon:

```text
users -> customers -> accounts -> transactions
users -> roles -> permissions
customers -> loans
customers -> cards
users -> notifications
users -> audit_logs
```

## 2. Auth/RBAC

### users

Luu tai khoan dang nhap.

```text
id UUID PK
email VARCHAR UNIQUE NOT NULL
password VARCHAR NOT NULL
full_name VARCHAR NOT NULL
status VARCHAR NOT NULL
created_at TIMESTAMP
updated_at TIMESTAMP
```

Status goi y:

```text
ACTIVE
LOCKED
DISABLED
PENDING_VERIFICATION
```

### roles

```text
id UUID PK
name VARCHAR UNIQUE NOT NULL
description VARCHAR
created_at TIMESTAMP
updated_at TIMESTAMP
```

Role goi y:

```text
CUSTOMER
STAFF
ADMIN
```

### permissions

```text
id UUID PK
name VARCHAR UNIQUE NOT NULL
description VARCHAR
created_at TIMESTAMP
updated_at TIMESTAMP
```

Permission goi y:

```text
ACCOUNT_READ_OWN
ACCOUNT_MANAGE
TRANSFER_CREATE
TRANSACTION_READ_OWN
CUSTOMER_READ
KYC_REVIEW
LOAN_CREATE
LOAN_REVIEW
LOAN_APPROVE
USER_MANAGE
ROLE_MANAGE
```

### user_roles

Bang noi user va role.

```text
id UUID PK
user_id UUID FK -> users.id
role_id UUID FK -> roles.id
created_at TIMESTAMP
```

### role_permissions

Bang noi role va permission.

```text
id UUID PK
role_id UUID FK -> roles.id
permission_id UUID FK -> permissions.id
created_at TIMESTAMP
```

### refresh_tokens

Dung cho refresh token va logout.

```text
id UUID PK
user_id UUID FK -> users.id
token VARCHAR UNIQUE NOT NULL
expires_at TIMESTAMP NOT NULL
revoked BOOLEAN DEFAULT false
created_at TIMESTAMP
revoked_at TIMESTAMP
```

## 3. Customer/KYC

### customers

Ho so khach hang. Mot `user` co the co mot `customer` profile.

```text
id UUID PK
user_id UUID FK -> users.id UNIQUE
customer_code VARCHAR UNIQUE
full_name VARCHAR NOT NULL
date_of_birth DATE
gender VARCHAR
phone VARCHAR
email VARCHAR
status VARCHAR NOT NULL
created_at TIMESTAMP
updated_at TIMESTAMP
```

Status goi y:

```text
ACTIVE
INACTIVE
BLACKLISTED
PENDING_KYC
```

### customer_addresses

```text
id UUID PK
customer_id UUID FK -> customers.id
address_type VARCHAR
line1 VARCHAR
line2 VARCHAR
ward VARCHAR
district VARCHAR
city VARCHAR
country VARCHAR
created_at TIMESTAMP
updated_at TIMESTAMP
```

Address type:

```text
PERMANENT
CURRENT
WORK
```

### kyc_documents

```text
id UUID PK
customer_id UUID FK -> customers.id
document_type VARCHAR NOT NULL
document_number VARCHAR NOT NULL
issued_date DATE
expired_date DATE
issued_by VARCHAR
status VARCHAR NOT NULL
created_at TIMESTAMP
updated_at TIMESTAMP
```

Document type:

```text
NATIONAL_ID
PASSPORT
DRIVER_LICENSE
```

Status:

```text
PENDING
VERIFIED
REJECTED
EXPIRED
```

## 4. Account

### accounts

Tai khoan ngan hang cua customer.

```text
id UUID PK
customer_id UUID FK -> customers.id
account_number VARCHAR UNIQUE NOT NULL
account_type VARCHAR NOT NULL
currency VARCHAR NOT NULL
balance DECIMAL(19,2) NOT NULL
status VARCHAR NOT NULL
opened_at TIMESTAMP
closed_at TIMESTAMP
created_at TIMESTAMP
updated_at TIMESTAMP
```

Account type:

```text
PAYMENT
SAVING
LOAN
```

Status:

```text
ACTIVE
LOCKED
CLOSED
PENDING
```

Quy tac:

- `balance` khong am neu khong cho thau chi.
- Moi thay doi tien nen di qua transaction.

## 5. Transaction/Transfer

### transactions

Luu lich su giao dich tien.

```text
id UUID PK
transaction_code VARCHAR UNIQUE NOT NULL
from_account_id UUID FK -> accounts.id NULL
to_account_id UUID FK -> accounts.id NULL
amount DECIMAL(19,2) NOT NULL
currency VARCHAR NOT NULL
transaction_type VARCHAR NOT NULL
status VARCHAR NOT NULL
description VARCHAR
created_by UUID FK -> users.id
created_at TIMESTAMP
updated_at TIMESTAMP
```

Transaction type:

```text
DEPOSIT
WITHDRAW
TRANSFER
FEE
INTEREST
LOAN_DISBURSEMENT
LOAN_REPAYMENT
```

Status:

```text
PENDING
SUCCESS
FAILED
CANCELLED
```

Quy tac transfer:

- Check account nguon ton tai.
- Check account dich ton tai.
- Check account nguon/dich ACTIVE.
- Check amount > 0.
- Check balance du.
- Tru tien nguon va cong tien dich trong cung transaction database.
- Tao transaction record de trace.

## 6. Loan

### loan_products

Loai san pham vay.

```text
id UUID PK
code VARCHAR UNIQUE NOT NULL
name VARCHAR NOT NULL
interest_rate DECIMAL(5,2) NOT NULL
min_amount DECIMAL(19,2)
max_amount DECIMAL(19,2)
min_term_months INT
max_term_months INT
status VARCHAR NOT NULL
created_at TIMESTAMP
updated_at TIMESTAMP
```

### loans

Ho so/khoan vay cua customer.

```text
id UUID PK
loan_code VARCHAR UNIQUE NOT NULL
customer_id UUID FK -> customers.id
loan_product_id UUID FK -> loan_products.id
principal_amount DECIMAL(19,2) NOT NULL
interest_rate DECIMAL(5,2) NOT NULL
term_months INT NOT NULL
outstanding_balance DECIMAL(19,2)
status VARCHAR NOT NULL
approved_by UUID FK -> users.id NULL
approved_at TIMESTAMP NULL
created_at TIMESTAMP
updated_at TIMESTAMP
```

Loan status:

```text
DRAFT
PENDING_REVIEW
APPROVED
REJECTED
DISBURSED
CLOSED
OVERDUE
```

### loan_repayment_schedules

Lich tra no.

```text
id UUID PK
loan_id UUID FK -> loans.id
period_no INT NOT NULL
due_date DATE NOT NULL
principal_due DECIMAL(19,2) NOT NULL
interest_due DECIMAL(19,2) NOT NULL
total_due DECIMAL(19,2) NOT NULL
paid_amount DECIMAL(19,2) DEFAULT 0
status VARCHAR NOT NULL
created_at TIMESTAMP
updated_at TIMESTAMP
```

Status:

```text
UNPAID
PARTIAL_PAID
PAID
OVERDUE
```

### loan_payments

Lich su thanh toan khoan vay.

```text
id UUID PK
loan_id UUID FK -> loans.id
schedule_id UUID FK -> loan_repayment_schedules.id NULL
account_id UUID FK -> accounts.id
amount DECIMAL(19,2) NOT NULL
payment_date TIMESTAMP NOT NULL
transaction_id UUID FK -> transactions.id NULL
status VARCHAR NOT NULL
created_at TIMESTAMP
```

## 7. Card

### cards

Thong tin the co ban.

```text
id UUID PK
customer_id UUID FK -> customers.id
account_id UUID FK -> accounts.id
card_number_masked VARCHAR NOT NULL
card_type VARCHAR NOT NULL
status VARCHAR NOT NULL
daily_limit DECIMAL(19,2)
issued_at TIMESTAMP
expired_at DATE
created_at TIMESTAMP
updated_at TIMESTAMP
```

Card type:

```text
DEBIT
CREDIT
PREPAID
```

Status:

```text
ACTIVE
LOCKED
EXPIRED
CANCELLED
```

Luu y:

- Khong luu full card number neu khong can.
- Chi luu masked number, vi du `**** **** **** 1234`.

## 8. Notification

### notifications

```text
id UUID PK
user_id UUID FK -> users.id
title VARCHAR NOT NULL
content TEXT NOT NULL
notification_type VARCHAR NOT NULL
channel VARCHAR NOT NULL
status VARCHAR NOT NULL
read_at TIMESTAMP NULL
created_at TIMESTAMP
```

Type:

```text
SECURITY
TRANSACTION
LOAN
SYSTEM
```

Channel:

```text
IN_APP
EMAIL
SMS
```

Status:

```text
PENDING
SENT
FAILED
READ
```

## 9. Audit Log

### audit_logs

Luu hanh dong quan trong de trace va bao mat.

```text
id UUID PK
actor_user_id UUID FK -> users.id NULL
action VARCHAR NOT NULL
resource_type VARCHAR
resource_id UUID
ip_address VARCHAR
user_agent VARCHAR
description TEXT
created_at TIMESTAMP
```

Action goi y:

```text
LOGIN_SUCCESS
LOGIN_FAILED
LOGOUT
REGISTER
TRANSFER_CREATED
ACCOUNT_LOCKED
LOAN_APPROVED
ROLE_CHANGED
PASSWORD_CHANGED
```

## 10. Thu tu nen lam trong project

Nen lam theo thu tu nay de vua hoc vua code khong bi roi:

```text
1. users
2. roles
3. permissions
4. user_roles
5. role_permissions
6. refresh_tokens
7. customers
8. accounts
9. transactions
10. notifications
11. loans + loan schedules
12. cards
13. audit_logs
```

## 11. Ban toi thieu de bat dau Auth ngay

Neu muon code Auth/JWT/RBAC truoc, chi can cac bang:

```text
users
roles
permissions
user_roles
role_permissions
refresh_tokens
```

Chua can tao het Loan/Card/Notification ngay.

## 12. Nguyen tac quan trong

- Password luu hash, khong luu plain text.
- Token/secret khong log ra console.
- Giao dich tien phai co `@Transactional`.
- Khong xoa transaction/audit log tuy tien.
- User chi duoc xem du lieu cua minh neu la CUSTOMER.
- Role/permission dung de check API.
- Audit log dung de biet ai da lam gi, luc nao.

