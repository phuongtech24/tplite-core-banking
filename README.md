# TPLite Core Banking Demo

Day la du an demo de hoc va luyen Java Spring Boot theo huong core banking co ban.
Muc tieu chinh la nam chac Java Core, OOP, Spring Boot, Spring Security, JPA, database transaction, phan quyen va cach to chuc module backend.

> Luu y: day khong phai he thong ngan hang thuc te. Banking production can rat nhieu lop nghiep vu, bao mat, doi soat, audit, monitoring, compliance va tich hop noi bo phuc tap hon.

## Tech Stack

| Nhom | Cong nghe |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 3.5.x |
| Security | Spring Security, JWT, RBAC/permission |
| Database | PostgreSQL |
| ORM | Spring Data JPA, Hibernate |
| Migration/seed | Flyway, application seed data |
| Messaging | Apache Kafka, Spring Kafka |
| API docs | Swagger/OpenAPI with springdoc |
| CI/CD | GitHub Actions CI |
| Build/test | Maven, JUnit 5, Mockito |

## Tinh Nang Chinh

### Auth/Security

- Dang ky, dang nhap, refresh token, logout.
- Ma hoa password bang BCrypt.
- JWT authentication filter doc token tu `Authorization: Bearer ...`.
- `UserDetailsService` load user, role va permission tu database.
- RBAC/permission voi `hasRole(...)` va `hasAuthority(...)`.
- Xu ly loi bao mat rieng cho `401 Unauthorized` va `403 Forbidden`.

### User, Customer, KYC

- Xem/cap nhat thong tin ca nhan.
- Admin/staff tim kiem user co phan trang va keyword.
- Customer profile va KYC document.
- Staff/admin duyet hoac tu choi KYC.

### Account, Card

- Tao tai khoan thanh toan cho customer.
- Xem danh sach tai khoan cua minh.
- Quan ly trang thai tai khoan.
- Phan biet `balance`, `frozenAmount`, `availableBalance`.
- Phat hanh the demo gan voi account.
- Xem danh sach the cua minh, cap nhat trang thai the.

### Transfer/Transaction

- Chuyen tien giua hai tai khoan.
- Kiem tra chu so huu, trang thai tai khoan, tien te va so du.
- Check available balance theo cong thuc `balance - frozenAmount`.
- Ho tro header `Idempotency-Key` de tranh duplicate transfer.
- Dung `@Transactional` de dam bao ACID.
- Dung pessimistic lock va thu tu lock theo UUID de giam nguy co deadlock.
- Luu lich su giao dich co phan trang.

### Loan

- Quan ly san pham vay mau.
- Customer tao ho so vay.
- Staff/admin duyet hoac tu choi ho so vay.
- Co Strategy Pattern cho tinh lai vay co ban.

### Notification, Audit, Admin

- Tao notification sau cac su kien quan trong.
- Kafka event cho notification demo.
- Audit log cho register, login, logout, transfer, loan.
- Admin dashboard thong ke nhanh tong user, customer, account, card, transaction, loan, notification va audit log.

## Cau Truc Du An

```text
Bank_Core/
|-- core-banking/
|   |-- src/main/java/com/tplite/core_banking/
|   |   |-- common/              # response, exception, config, seed data
|   |   |-- module/
|   |   |   |-- auth/
|   |   |   |-- user/
|   |   |   |-- customer/
|   |   |   |-- account/
|   |   |   |-- card/
|   |   |   |-- transfer/
|   |   |   |-- loan/
|   |   |   |-- notification/
|   |   |   |-- audit/
|   |   |   |-- admin/
|   |-- src/main/resources/
|   |   |-- application.yml
|   |   |-- db/migration/
|   |-- src/test/java/
|-- infra/docker/docker-compose.kafka.yml
```

## Chay Local

### 1. Chuan bi PostgreSQL

Tao database sach, vi project dang dung UUID cho id:

```sql
CREATE DATABASE tplite_db;
```

Neu may da co schema cu bi lech kieu du lieu, nen tao database moi hoac drop schema cu truoc khi chay.

### 2. Cau hinh database

Dung bien moi truong hoac tao file local `.env` tu `core-banking/.env.example`:

```env
DB_URL=jdbc:postgresql://localhost:5432/tplite_core_banking_dev
DB_USERNAME=postgres
DB_PASSWORD=your_local_password
JWT_SECRET=change_me_to_at_least_32_bytes_secret_key
```

### 3. Chay Kafka neu muon test notification event

```bash
docker compose -f infra/docker/docker-compose.kafka.yml up -d
```

Neu Docker khong pull duoc image tu Docker Hub, day la loi DNS/proxy/network local, khong phai loi code.

### 4. Chay test va app

```bash
cd core-banking
mvn test
mvn spring-boot:run
```

App mac dinh chay tai:

```text
http://localhost:8081
```

Swagger UI:

```text
http://localhost:8081/swagger-ui.html
```

## CI/CD

Du an co GitHub Actions workflow tai `.github/workflows/ci.yml`.

Workflow hien tai chay khi push hoac pull request vao `main`, `master`, `develop`:

```text
checkout source
setup Java 17
cache Maven dependencies
mvn -B test
```

Day la CI co ban cho demo. Chua co buoc deploy production.

## Seed Data

Khi `app.seed.enabled=true`, app se tao data mau neu chua ton tai.

| Role | Email | Password |
| --- | --- | --- |
| Admin | admin@tplite.vn | Password@123 |
| Staff | staff@tplite.vn | Password@123 |
| Customer | customer@tplite.vn | Password@123 |

Seed data co them:

- Role/permission co ban.
- Customer mau da ACTIVE.
- Account mau cho customer.
- Loan product mau.

## API Flow De Test Nhanh

1. Login: `POST /api/auth/login`.
2. Lay token trong response.
3. Goi API bang header:

```text
Authorization: Bearer <access_token>
```

4. Thu cac flow:

- Customer: profile, KYC, account, card, transfer, loan, notification.
- Staff/admin: duyet KYC, duyet loan, xem audit log.
- Admin: dashboard, quan ly user/status.

Khi test transfer nen gui them header:

```text
Idempotency-Key: <uuid>
```

## Nhung Diem Hoc Duoc

- Vi sao password phai hash, khong luu plain text.
- Vi sao register/transfer/approve loan can `@Transactional`.
- Cach Spring Security dua user vao `SecurityContext`.
- Cach load role/permission tu DB bang `UserDetailsService`.
- Cach phan trang voi `Pageable`, tranh API `getAll`.
- Cach phan biet ledger balance, frozen amount va available balance.
- Cach dung idempotency key de chong double submit giao dich.
- Cach dung lock trong giao dich tien.
- Cach ap dung Strategy Pattern trong bai toan lai vay.
- Cach dung Kafka o muc event-driven co ban.



## Commit Convention

Du an uu tien Conventional Commits:

```text
feat: add card issuing API
fix: prevent transfer to inactive account
docs: update README limitations
test: add loan interest strategy test
chore: update kafka compose config
```
