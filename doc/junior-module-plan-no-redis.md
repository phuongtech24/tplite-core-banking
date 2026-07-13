# Junior module plan - Spring Boot core banking, bo Redis

Muc tieu: hoan thien project theo huong junior backend Java/Spring. Project can co security, CRUD nghiep vu, transaction, phan quyen, message queue Kafka, test, doc API, va cach giai thich duoc khi phong van.

Scope hien tai:
- Giu: Spring Boot, Spring Security, JWT, RBAC, MySQL/PostgreSQL, Flyway, Kafka, Docker, CI/CD, unit/integration test.
- Bo tam thoi: Redis cache.
- OAuth2/Google: nice-to-have, lam sau khi core module da on.

## Phase 1. Security/Auth hoan chinh

Trang thai hien tai:
- Register.
- Login.
- JWT access token.
- Refresh token luu DB.
- Logout revoke refresh token.
- RBAC role/permission tu DB.

Can bo sung:
- `GlobalExceptionHandler` tra loi chuan cho 400, 401, 403, 404, 409, 500.
- `AuthenticationEntryPoint` cho loi chua dang nhap.
- `AccessDeniedHandler` cho loi khong du quyen.
- Endpoint test role:
  - `GET /api/customer/profile` can `ROLE_CUSTOMER`.
  - `GET /api/admin/users` can `ROLE_ADMIN`.
  - `GET /api/staff/kyc` can `ROLE_STAFF`.
- Seed them user demo bang Flyway.

Kien thuc can on:
- Password hash voi BCrypt.
- JWT stateless.
- Refresh token stateful.
- `SecurityContext`.
- `UserDetailsService`.
- `hasRole` vs `hasAuthority`.
- Proxy Pattern trong `@Transactional` va Spring Security filter chain.

## Phase 2. User/Customer module

Muc tieu:
- Quan ly thong tin user/customer.
- Customer xem va sua profile cua minh.
- Staff/admin xem danh sach customer.

Bang lien quan:
- `users`
- `customers`
- `kyc_documents`

API can co:
- `GET /api/me`
- `PUT /api/me`
- `GET /api/admin/users`
- `GET /api/admin/users/{id}`
- `PATCH /api/admin/users/{id}/status`
- `POST /api/customers/kyc-documents`
- `PATCH /api/staff/kyc-documents/{id}/review`

Kien thuc can on:
- DTO request/response.
- Validation.
- Pagination voi `Pageable`.
- Search/filter theo email, status, phone.
- RBAC: user chi thao tac du lieu cua minh.

## Phase 3. Account module

Muc tieu:
- Tao tai khoan ngan hang co ban.
- Xem danh sach tai khoan.
- Khoa/mo/dong tai khoan.
- Xem so du.

Bang lien quan:
- `accounts`
- `account_types`

API can co:
- `POST /api/accounts`
- `GET /api/accounts/my`
- `GET /api/accounts/{id}`
- `PATCH /api/accounts/{id}/status`

Rule nghiep vu:
- Account number phai unique.
- Account moi tao mac dinh `ACTIVE` hoac `PENDING`.
- Chi customer so huu moi xem duoc account cua minh.
- Staff/admin co quyen xem danh sach.

Kien thuc can on:
- Unique constraint.
- Enum status.
- Factory Method co the ap dung khi tao nhieu loai account.

## Phase 4. Transfer/Transaction module

Muc tieu:
- Chuyen tien noi bo.
- Tru tien tai khoan nguon.
- Cong tien tai khoan dich.
- Luu transaction ledger.
- Dam bao ACID.

Bang lien quan:
- `accounts`
- `transfers`
- `transactions`
- `audit_logs`

API can co:
- `POST /api/transfers`
- `GET /api/transfers/my`
- `GET /api/transfers/{id}`
- `GET /api/transactions/my`

Rule nghiep vu:
- Amount > 0.
- Source account phai ACTIVE.
- Destination account phai ACTIVE.
- Khong du so du thi reject.
- Neu buoc cong tien loi thi rollback ca giao dich.

Ky thuat quan trong:
- `@Transactional`.
- Locking khi cap nhat so du:
  - Ban dau co the dung pessimistic lock.
  - Sau do hoc optimistic lock voi `@Version`.
- Idempotency key de tranh double submit.

Kien thuc can on:
- ACID.
- Race condition.
- Isolation level.
- Pessimistic lock vs optimistic lock.

## Phase 5. Loan module

Muc tieu:
- Customer tao yeu cau vay.
- Staff review.
- Admin/manager approve/reject.

Bang lien quan:
- `loans`
- `loan_applications`
- `loan_status_history`

API can co:
- `POST /api/loans/applications`
- `GET /api/loans/my`
- `GET /api/staff/loans`
- `PATCH /api/staff/loans/{id}/review`
- `PATCH /api/admin/loans/{id}/approve`
- `PATCH /api/admin/loans/{id}/reject`

Workflow:
- `PENDING`
- `UNDER_REVIEW`
- `APPROVED`
- `REJECTED`
- `DISBURSED`

Kien thuc can on:
- State transition.
- Strategy Pattern co the ap dung khi tinh lai suat theo loai loan.
- Audit log cho moi lan doi status.

## Phase 6. Notification module voi Kafka

Muc tieu:
- Khi co su kien register, transfer thanh cong, loan duoc duyet, he thong tao notification.
- Dung Kafka de tach xu ly thong bao ra khoi business transaction chinh.

Topic goi y:
- `user.registered`
- `transfer.completed`
- `loan.status.changed`
- `notification.created`

Luong:
1. Business service hoan thanh nghiep vu.
2. Publish event vao Kafka.
3. Notification consumer nhan event.
4. Luu notification vao DB.
5. API tra danh sach notification cho user.

API can co:
- `GET /api/notifications/my`
- `PATCH /api/notifications/{id}/read`
- `PATCH /api/notifications/read-all`

Kien thuc can on:
- Message queue la gi.
- Producer/consumer.
- Async processing.
- At-least-once delivery.
- Idempotent consumer de tranh tao trung notification.

Ghi chu:
- Chua can Redis.
- Chua can websocket.
- Kafka o muc junior chi can event basic va consumer luu DB.

## Phase 7. Audit log module

Muc tieu:
- Luu hanh dong quan trong phuc vu tra soat.

Can log:
- Login fail/success.
- Register.
- Change user status.
- Create transfer.
- Approve/reject loan.
- Revoke refresh token/logout.

Bang lien quan:
- `audit_logs`

API can co:
- `GET /api/admin/audit-logs`

Kien thuc can on:
- Cross-cutting concern.
- Co the dung AOP sau khi ban service chay on.
- Proxy Pattern trong Spring AOP.

## Phase 8. Error handling, validation, API contract

Muc tieu:
- API tra loi dong nhat, de frontend consume.

Can lam:
- `GlobalExceptionHandler`.
- Custom exception:
  - `ResourceNotFoundException`
  - `BusinessException`
  - `UnauthorizedException`
  - `ForbiddenException`
  - `DuplicateResourceException`
- Validate request bang annotation.
- Error response co:
  - `success`
  - `message`
  - `code`
  - `data`
  - `timestamp`
  - `path`

Kien thuc can on:
- HTTP status code.
- 400 vs 401 vs 403 vs 404 vs 409.
- Khong tra stack trace ra client.

## Phase 9. Testing

Muc tieu:
- Co test de chung minh code dang tin.

Can test:
- Unit test service:
  - register duplicate email.
  - login wrong password.
  - transfer insufficient balance.
  - loan invalid status transition.
- Integration test controller:
  - auth endpoint.
  - protected endpoint without token -> 401.
  - protected endpoint wrong role -> 403.
- Repository test cho query quan trong.

Tool:
- JUnit 5.
- Mockito.
- Spring Boot Test.
- Testcontainers neu co thoi gian.

Kien thuc can on:
- Unit test vs integration test.
- Mock la gi.
- Arrange/Act/Assert.

## Phase 10. Docker, CI/CD, deploy basic

Muc tieu:
- Project co the chay de dang tren may khac.

Can lam:
- `Dockerfile` cho Spring Boot app.
- `docker-compose.yml` gom:
  - app
  - postgres
  - kafka
  - zookeeper hoac kafka kraft
- GitHub Actions hoac Jenkinsfile:
  - checkout code.
  - build.
  - run test.
  - package jar.

Kien thuc can on:
- Docker image/container.
- Port mapping.
- Environment variable.
- CI/CD pipeline.
- Build fail khi test fail.

## Thu tu lam de nhanh ma dung

1. `GlobalExceptionHandler` va security error handler.
2. User/Customer profile.
3. Account module.
4. Transfer/Transaction module co `@Transactional`.
5. Loan module basic.
6. Notification DB basic.
7. Kafka cho notification event.
8. Audit log.
9. Test cac case quan trong.
10. Docker compose.
11. CI/CD basic.
12. README + API examples.

## Checklist phong van sau moi module

Sau moi module, phai tra loi duoc:
- Module nay giai quyet nghiep vu gi?
- Bang DB nao lien quan?
- API input/output la gi?
- Dung collection nao: `List`, `Set`, `Map`, vi sao?
- Co can `@Transactional` khong, vi sao?
- Co ap dung design pattern nao khong?
- Loi nao co the xay ra?
- Neu nhieu request cung luc thi co race condition khong?
- Co can Kafka khong, neu co thi event nao?
- Test case quan trong nhat la gi?
