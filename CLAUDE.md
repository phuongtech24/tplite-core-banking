# CLAUDE.md

Guidelines for AI assistants working on this project.

Project goal: build a basic but realistic Spring Boot core banking project while helping the developer learn Java Core, OOP, Spring Boot, Spring Security, JPA, database fundamentals, and practical backend reasoning.

These rules are adapted from a general "think before coding / simplicity / surgical changes / goal-driven execution" style, but customized for this learning project. Prefer clear, explainable junior-level engineering over clever or over-engineered solutions.

## 1. Think Before Coding

Do not jump straight into code when the task has hidden business, security, database, or learning trade-offs. First understand the module, the data flow, and what concept the user is trying to learn.

Before implementing:

- Identify the module: `auth`, `user`, `customer`, `account`, `transfer`, `loan`, `notification`, `audit`, or infra.
- State the business goal in plain language.
- Name the input, output, database tables, and permissions involved.
- Call out risk areas:
  - authentication/authorization
  - money movement
  - transaction boundaries
  - race condition/lost update
  - pagination/search
  - sensitive data leakage
- If the task has multiple valid designs, explain the options briefly.
- If the user is learning, include a short "why this design" explanation.
- If the task is unclear and a wrong assumption would create bad code, ask one focused question.

For every non-trivial task, use this quick mental checklist:

- What API do we expose?
- What DTOs are needed?
- What service method owns the business rule?
- What repository query is needed?
- Does this need `@Transactional`?
- Does this need pagination?
- Does this need role/permission checks?
- What can go wrong?

Example:

```text
Task: transfer money.
Module: transfer.
Input: fromAccountId, toAccountId, amount.
Output: transaction result.
DB writes: accounts, transactions.
Risk: insufficient balance, lost update, deadlock.
Need: @Transactional + account lock + ownership check.
```

## 2. Simplicity With Learning Value

Write the minimum code that solves the real use case and teaches the right backend concept. Do not add advanced infrastructure or abstractions just because they sound senior.

Prefer:

- Monolith Spring Boot first.
- Clear Controller -> Service -> Repository flow.
- DTOs for request/response.
- Simple `Pageable` pagination for list APIs.
- `@Transactional` at service layer.
- Basic RBAC with role/permission tables.
- One clear implementation first, then extend with pattern only when useful.

Avoid:

- Microservices before the monolith is clean.
- Redis for every problem.
- Kafka for synchronous business rules.
- Complex generic abstractions before at least two real use cases exist.
- Returning JPA entities directly from controllers.
- `getAll` APIs for potentially large tables.
- Logging password, JWT, refresh token, OTP, secret keys, or full card data.

When adding patterns, keep them practical:

- Strategy Pattern: use for interest calculation, fee calculation, notification channels, payment methods.
- Factory Method: use for creating account/card/notification types when creation logic branches by type.
- Proxy Pattern: explain Spring `@Transactional`, `@Cacheable`, `@Async`, and AOP.
- Singleton Pattern: explain Spring beans.
- Builder Pattern: use when object construction becomes noisy or has many optional fields.

Do not force patterns into simple CRUD.

## 3. Surgical Changes And Project Discipline

Touch only what is needed for the current task. Keep the codebase easy to review and easy to explain in an interview.

When editing:

- Read existing code before changing it.
- Match current package style and naming.
- Keep modules separated:
  - Controller: request/response only.
  - Service: business rules.
  - Repository: database access.
  - DTO: API contract.
  - Entity: database mapping.
- Do not refactor unrelated code unless the user asks.
- Do not remove user changes.
- Remove only unused code/imports introduced by your own change.
- If an unrelated issue is found, mention it instead of fixing silently.

For database and JPA:

- Use `Page<T>` or `Slice<T>` for list APIs.
- Avoid unbounded `findAll()` in API paths.
- Add search/filter parameters intentionally.
- Add indexes/migrations when the query becomes important.
- Keep money fields as `BigDecimal`.
- Use database transaction for multi-write business operations.
- Use optimistic or pessimistic locking when concurrent updates can corrupt state.

For security:

- Password must be hashed with `PasswordEncoder`.
- Never store plain-text password.
- JWT filter should only authenticate valid Bearer tokens.
- Load roles/permissions through `UserDetailsService`.
- Use `hasRole` for `ROLE_...`.
- Use `hasAuthority` for permission names like `TRANSFER_CREATE`.
- Do not let users pass arbitrary `userId` to access another user's data when the user can be derived from JWT.

For API response and errors:

- Use the project `ApiResponse` format.
- Use `GlobalExceptionHandler`; do not catch exceptions in every controller.
- Return meaningful status:
  - `400` validation/business error
  - `401` unauthenticated
  - `403` authenticated but not allowed
  - `404` resource not found
  - `409` duplicate/conflict
  - `500` unexpected server error

## 4. Goal-Driven Execution And Verification

Every task should end with something verifiable: compile, test, API endpoint, or clear manual check.

Before coding, define success criteria:

- What endpoint or class should exist?
- What business rule should pass?
- What error case should be handled?
- What command verifies the change?

During implementation:

- Work in small steps.
- Compile after meaningful changes.
- Prefer adding one module slice end-to-end over many half-finished files.
- Keep user-facing updates short and useful.

After implementation, report:

- What changed.
- Which files matter.
- How to test quickly.
- What concept this teaches.
- What remains as next step.

Verification examples:

```text
mvn compile
```

```http
POST /api/auth/login
GET /api/accounts/my?page=0&size=20
POST /api/transfers
```

If tests are not written yet, say so clearly and give the manual API checks.

## Project Learning Rules

This project is both a codebase and a learning path. The assistant should help the user understand, not just generate code.

When the user asks to learn while coding:

- Explain the flow before or after code.
- Point to the exact class/method where the concept appears.
- Ask short review questions when useful.
- Compare simple explanation and technical explanation.
- Mention likely interview questions.

Core topics to reinforce:

- Java Core:
  - OOP: encapsulation, abstraction, inheritance, polymorphism.
  - Collections: `List`, `Set`, `Map`, `Queue`.
  - Exceptions: checked/unchecked, custom exceptions.
  - Immutability and object construction.
- Spring Boot Core:
  - Controller, Service, Repository.
  - Dependency Injection.
  - Bean lifecycle basics.
  - Configuration via `application.yml`.
- Spring Security:
  - Authentication vs authorization.
  - JWT.
  - Refresh token.
  - `SecurityContext`.
  - `UserDetailsService`.
  - RBAC and permission-based authorization.
- Database Core:
  - ACID.
  - Transaction isolation.
  - Indexes.
  - Pagination.
  - Optimistic lock vs pessimistic lock.
  - Unique constraints.
- Backend architecture:
  - DTO boundaries.
  - Error handling.
  - Logging.
  - Audit log.
  - Event-driven design with Kafka when useful.

## Module-Specific Rules

### Auth/Security

- Register hashes password.
- Login uses `AuthenticationManager` or Spring Security authentication flow.
- JWT access token is short-lived.
- Refresh token is stored/revoked server-side.
- Logout revokes refresh token.
- Protected endpoints rely on JWT identity, not request-supplied user identity.

### User/Customer/KYC

- `User` is login/account identity.
- `Customer` is banking profile/KYC identity.
- Loan should depend on an existing customer profile.
- KYC review should be staff/admin action.
- Customer should become `ACTIVE` only after KYC is verified.

### Account

- Account belongs to a user/customer.
- Account number must be unique.
- Account balance uses `BigDecimal`.
- Account status must be checked before transfer.
- List APIs must use pagination.

### Transfer/Transaction

- Must use `@Transactional`.
- Must check account ownership.
- Must check source/destination account status.
- Must check balance.
- Must lock accounts or otherwise handle concurrent updates.
- Must write transaction history.
- Notification/audit can be async later, but the money movement itself must stay atomic.

### Loan

- Loan product defines min/max amount, min/max term, and interest rate.
- Customer must be `ACTIVE` before applying.
- Loan application starts as `PENDING_REVIEW`.
- Admin approves/rejects.
- Interest calculation should use Strategy Pattern when multiple calculation methods appear.

### Notification/Kafka

- Start with DB notification if the project needs speed.
- Kafka is useful when actions should happen after the main transaction:
  - transfer success -> notification
  - loan approved -> notification
  - important action -> audit event
- Do not put core money update logic behind Kafka in this monolith stage.

## Redis And Kafka Guidance

Redis is optional for now. Suggest it only when there is a real fit:

- OTP with TTL.
- Rate limiting.
- Cache read-heavy, low-risk data.
- Avoid caching balance unless the consistency story is clear.

Kafka is allowed as junior nice-to-have:

- Good for notification and audit events.
- Good for learning producer/consumer.
- Not necessary for simple synchronous CRUD.
- Do not introduce Kafka before the module works synchronously.

## Pagination Rules

List APIs should not expose unbounded `getAll`.

Use:

- `page`
- `size`
- `sort`
- `keyword`
- status/type filters when relevant

Prefer:

- `Page<T>` when UI needs total count.
- `Slice<T>` when only "has next" matters.
- `List<T>` only for small bounded data such as enum-like configuration.

Default:

- `size=20`
- max `size=100`

## Final Response Style

When finishing a task, keep the response practical:

- Mention compile/test result.
- Mention the important files.
- Mention the endpoints or behavior.
- Mention the next best step.
- Keep explanations short unless the user asks for deeper teaching.

Do not overwhelm the user with every line changed. Focus on what helps them continue building and learning.
