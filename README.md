# 🏦 TPLite - Core Banking System

Dự án mô phỏng hệ thống Ngân hàng Lõi (Core Banking) tập trung vào tính toàn vẹn dữ liệu (ACID), xử lý đồng thời (Concurrency) và bảo mật.

## 🛠️ Tech Stack

| Tầng | Công nghệ |
|------|-----------|
| Runtime | Java 21 (LTS) |
| Framework | Spring Boot 3.2+ |
| ORM | Spring Data JPA + Hibernate 6.x |
| Database | PostgreSQL 15+ |
| Cache | Redis 7+ |
| Security | Spring Security 6.x + JWT |
| Migration | Flyway 9+ |
| Container | Docker + Docker Compose |
| CI/CD | GitHub Actions |
| Docs | Swagger / OpenAPI 3 |

## 📦 Modules

| Module | Chức năng | Kiến thức áp dụng |
|--------|-----------|------------------|
| **auth** | Đăng ký, đăng nhập, JWT | Spring Security, BCrypt, JWT |
| **account** | Mở tài khoản, xem số dư | JPA, `@Version` Optimistic Lock |
| **transfer** ⭐ | Chuyển tiền, kiểm tra hạn mức | ACID, Pessimistic Lock, chống Deadlock |
| **transaction** | Lịch sử GD, filter, phân trang | JPA Specification, Composite Index |
| **card** | Phát hành thẻ, khóa thẻ | State Machine, `@Transactional` |
| **loan** | Đăng ký vay, tính lãi suất | Business logic, Scheduled Job |
| **notification** | Email/SMS sau giao dịch | `@Async`, Event-driven |
| **admin** | Quản lý user, audit log | Spring AOP, RBAC |

## 🗂️ Cấu Trúc Dự Án

```
Bank_Core/
├── doc/                                         # Tài liệu & lộ trình học
│   ├── lo-trinh-on-tap-tpbank.md
│   └── bug-notes.md
│
└── core-banking/                                # Spring Boot Application
    └── src/
        ├── main/
        │   ├── java/com/tplite/core_banking/
        │   │   ├── CoreBankingApplication.java
        │   │   │
        │   │   ├── common/                      # Dùng chung toàn app
        │   │   │   ├── exception/               # Custom exceptions + GlobalExceptionHandler
        │   │   │   ├── response/                # ApiResponse wrapper
        │   │   │   └── util/                    # Tiện ích chung
        │   │   │
        │   │   ├── config/                      # Cấu hình Spring (Security, Swagger, Async)
        │   │   ├── security/                    # JWT Filter, UserDetailsService
        │   │   │
        │   │   └── module/                      # Business logic theo feature
        │   │       ├── auth/
        │   │       │   └── dto/
        │   │       ├── account/
        │   │       │   └── dto/
        │   │       ├── transfer/                # ⭐ Module quan trọng nhất
        │   │       │   └── dto/
        │   │       ├── card/
        │   │       │   └── dto/
        │   │       ├── loan/
        │   │       │   └── dto/
        │   │       ├── notification/
        │   │       │   └── dto/
        │   │       └── admin/
        │   │           └── dto/
        │   │
        │   └── resources/
        │       ├── application.yml
        │       ├── application-dev.yml
        │       └── db/migration/                # Flyway SQL scripts (V1__, V2__...)
        │
        └── test/
            └── java/com/tplite/core_banking/
                ├── module/
                │   ├── account/                 # Unit tests
                │   └── transfer/                # Unit tests (quan trọng nhất)
                └── integration/                 # Integration tests
```

> **Nguyên tắc tổ chức:** Mọi thứ liên quan đến một feature đều nằm trong cùng một folder module (Controller, Service, Repository, Entity, DTO). Dễ đọc, dễ scale, gần với chuẩn MSA thực tế.

---


## 📌 Quy chuẩn Git Commit (Commit Convention)
Dự án áp dụng chuẩn **Conventional Commits** để quản lý lịch sử mã nguồn thống nhất, rõ ràng và dễ dàng tracking.

### 1. Cú pháp cơ bản
`<type>: <mô tả ngắn gọn thay đổi (ưu tiên tiếng Anh hoặc tiếng Việt rõ nghĩa)>`

### 2. Danh sách các `type` sử dụng
* **`feat:`** Thêm một tính năng hoặc module mới.
  * *Ví dụ:* `feat: implement money transfer API`
* **`fix:`** Sửa một lỗi/bug.
  * *Ví dụ:* `fix: resolve N+1 query issue in transaction history`
* **`refactor:`** Tối ưu hóa, dọn dẹp hoặc cấu trúc lại code nhưng không làm thay đổi chức năng.
  * *Ví dụ:* `refactor: extract validation logic to separate service`
* **`docs:`** Cập nhật tài liệu (README, Swagger, JavaDoc).
  * *Ví dụ:* `docs: update API documentation for authentication`
* **`chore:`** Các công việc vặt, cấu hình hệ thống, cập nhật dependency (không đụng chạm tới source code chính).
  * *Ví dụ:* `chore: add Spring Security and JWT dependencies`
* **`test:`** Thêm mới hoặc sửa đổi các test case (Unit Test / Integration Test).
  * *Ví dụ:* `test: add unit tests for TransferService`

### 3. Quy tắc bổ sung
* Viết thường (lowercase) toàn bộ phần `<type>`.
* Mô tả ngắn gọn, đi thẳng vào vấn đề (không quá 72 ký tự).