# 🏦 Lộ Trình Ôn Tập & Luyện Phỏng Vấn Junior Backend Java — TPBank & Fintech

> **Mục tiêu:** Pass phỏng vấn Junior Backend Java Spring Boot tại TPBank và các công ty Fintech/Bank  
> **Thời gian:** 1–2 tháng  
> **Xác nhận từ Senior trong ngành:** _"Java Core + Spring + DB (Transaction, ACID, Index) — ôn chắc cái đó là đủ Junior"_

---

## 📋 Tổng Quan Lộ Trình

```
Java Core  →  Spring Boot  →  Database (ACID/Index/Lock)  →  MSA Cơ Bản (hiểu là đủ)  →  DSA Song Song  →  Mock Interview
```

> 🎯 **Mục tiêu & Chiến lược 2 đợt (cập nhật 09/07/2026):**
> - **Đợt 1 — Giữa tháng 8 (Công ty level thấp hơn):** Đi thực chiến để *luyện*. Mục đích là quen môi trường phỏng vấn, phát hiện lỗ hổng kiến thức, rút kinh nghiệm. **Không quá áp lực kết quả** — coi như "tập bắn".
> - **Đợt 2 — Giữa tháng 9 (TPBank):** Mục tiêu chính. Lúc này đã có "máu" từ đợt 1 → tự tin & chín chắn hơn.
> - **Nguyên tắc xuyên suốt:** *Ôn và Thực hành song song* — mỗi tuần vừa học lý thuyết vừa code dự án BankCore / làm bài tập để kiến thức "nằm trong tay", không chỉ nằm trên giấy.

| Tuần | Thời gian | Mục tiêu chính | Giờ/tuần | Ghi chú |
|------|-----------|----------------|----------|---------|
| T1–2 | 09/07 – 22/07 | Java Core vững nền tảng | 12–15h | Ôn + code thuật toán hàng ngày |
| T3–4 | 23/07 – 05/08 | Spring Boot thực chiến | 12–15h | Gắn chặt vào dự án BankCore |
| T5–6 | 06/08 – 19/08 | Database chuyên sâu (ACID/Index/Lock) | 12–15h | 🚀 **Đợt 1: Giữa T8 phỏng vấn công ty nhỏ** |
| T7 | 20/08 – 26/08 | Security & API hoàn chỉnh | 10–12h | Rút kinh nghiệm đợt 1, vá lỗ hổng |
| T8 | 27/08 – 02/09 | Polish dự án + DSA tăng tốc | 10–12h | |
| T9 | 03/09 – 09/09 | TPBank deep-dive (DB + Spring hỏi sâu) | 12–15h | Tập trung điểm TPBank hay hỏi |
| T10 | 10/09 – 16/09 | Mock Interview + chốt | 12–15h | 🎯 **Đợt 2: Giữa T9 phỏng vấn TPBank** |

---

## 📅 Lịch Làm Việc Hàng Ngày

| Thời gian | Hoạt động |
|-----------|-----------|
| 08:00–09:30 | **Dự án BankCore (TPLite)** — session quan trọng nhất |
| 11:30–13:00 | Nghỉ trưa — bắt buộc |
| 15:00–18:00 | Đồ án tốt nghiệp (deadline 29/06) |
| 19:30–21:30 | Học tiếng Nhật — Minna no Nihongo |
| **Xuyên suốt** | Nạp kiến thức Core (Java, Spring, DB) — lồng ghép khi cần |

> **Nguyên tắc:** Mỗi bug fix = 1 session học hiệu quả nhất. Ghi note 3 dòng: **Vấn đề → Nguyên nhân → Giải pháp**

### 🔁 Nguyên Tắc "Ôn + Thực Hành Song Song" (Quan Trọng Nhất)

> Học lý thuyết không thôi = **nhanh quên**. Phải code ngay để kiến thức "nằm trong tay".

| Hoạt động | Tần suất | Cách làm |
|-----------|----------|----------|
| Ôn lý thuyết (đọc note/file này) | Hàng ngày 30p | Đọc 1 mục, tự nhớ lại bằng miệng trước khi xem đáp án |
| Code dự án BankCore | Mỗi ngày 08:00–09:30 | Áp dụng đúng kiến thức đang học (vd: đang học Lock → refactor `TransferService`) |
| Làm 1–3 bài LeetCode | Hàng ngày (xen kẽ) | Theo lộ trình DSA ở PHẦN V |
| Viết lại code từ đầu (không copy) | Cuối tuần mỗi phần | Code lại `TransferService`, `Account` để nhớ sâu |
| Tự hỏi – tự trả lời (mock) | 2–3 lần/tuần | Đóng vai phỏng vấn, ghi âm để biết mình diễn đạt thế nào |

> 💡 **Mẹo thực hành tận mắt:** Mỗi khi học 1 khái niệm (vd: Isolation Level) → lập tức mở PostgreSQL thực hành `SET TRANSACTION ISOLATION LEVEL ...` để **tận mắt thấy** Dirty Read / Non-repeatable Read. Học Lock → chạy 2 tab psql cùng UPDATE 1 row để thấy block/deadlock.

---

## 🔷 PHẦN I — Java Core

### OOP — 4 Tính Chất (hỏi 100% phỏng vấn)

| Tính chất | Định nghĩa | Ví dụ trong BankCore |
|-----------|-----------|----------------------|
| **Encapsulation** | Ẩn data, chỉ expose qua method | `Account.balance` là private; chỉ thay đổi qua `debit()`/`credit()` |
| **Inheritance** | Kế thừa thuộc tính và phương thức | `SavingsAccount extends Account` — có thêm lãi suất |
| **Polymorphism** | Cùng tên method, hành vi khác nhau | `PaymentStrategy.pay()` — CreditCard/DebitCard/Transfer khác nhau |
| **Abstraction** | Ẩn chi tiết, lộ interface | `TransferService` chỉ biết gọi `transferMoney()`, không biết bên trong làm gì |

### Collections — Dùng Đúng Cấu Trúc

```java
// HashMap: O(1) lookup — dùng cho cache nhỏ, mapping
Map<String, Account> accountCache = new ConcurrentHashMap<>(); // Thread-safe!

// ArrayList vs LinkedList:
List<Transaction> history = new ArrayList<>();   // O(1) get(i), dùng cho read nhiều
Deque<Transaction> pending = new LinkedList<>(); // O(1) add/remove đầu, dùng cho queue

// TreeMap: Tự sắp xếp theo key — dùng cho lịch sử sắp xếp theo thời gian
TreeMap<LocalDate, List<Transaction>> byDate = new TreeMap<>();

// HashMap hoạt động: hashCode() tính bucket → equals() xử lý collision
// Load factor 0.75 → resize khi > 75% dung lượng
```

### Exception Handling

```java
// Checked (extends Exception): Bắt buộc xử lý → IOException, SQLException
// Unchecked (extends RuntimeException): Không bắt buộc → NullPointerException, domain exceptions

// Spring @Transactional CHỈ rollback Unchecked theo mặc định!
public class InsufficientFundsException extends RuntimeException { ... }
```

### Concurrency — Core của Hệ Thống Ngân Hàng

```java
// Vấn đề Race Condition: T1 đọc balance=1000, T2 đọc balance=1000
// T1 rút 800→200, T2 rút 900→100 (SAI! Tổng rút 1700 > 1000)

// Giải pháp 1: synchronized
public synchronized void debit(BigDecimal amount) { ... }

// Giải pháp 2: @Version Optimistic Lock (tốt nhất cho JPA)
@Entity public class Account {
    @Version private Long version; // JPA tự kiểm tra và tăng version
}

// Giải pháp 3: SELECT FOR UPDATE — Pessimistic Lock
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT a FROM Account a WHERE a.id = :id")
Optional<Account> findByIdWithLock(@Param("id") Long id);
```

### Stream API

```java
// Tính tổng tiền chuyển đi trong tháng:
BigDecimal totalOut = transactions.stream()
    .filter(t -> t.getType() == TRANSFER && t.getFromAccountId().equals(accountId))
    .filter(t -> t.getCreatedAt().getMonth() == LocalDate.now().getMonth())
    .map(Transaction::getAmount)
    .reduce(BigDecimal.ZERO, BigDecimal::add);

// Nhóm theo ngày:
Map<LocalDate, List<Transaction>> byDate = transactions.stream()
    .collect(Collectors.groupingBy(t -> t.getCreatedAt().toLocalDate()));
```

---

## 🔷 PHẦN II — Spring Boot

### IoC & Dependency Injection

| Khái niệm | Giải thích | Ví dụ |
|-----------|-----------|-------|
| IoC Container | Spring quản lý vòng đời object thay vì bạn tự `new()` | Spring tạo và quản lý `TransferService` |
| Constructor Injection | Tốt nhất: dễ test, final field | `@RequiredArgsConstructor` |
| Bean Scope | Singleton (default): 1 instance dùng chung | Coi chừng state trong Singleton + multi-thread |

### @Transactional — Senior sẽ hỏi sâu

```java
// Đặt ở Service layer. KHÔNG đặt ở Controller, KHÔNG đặt ở Repository.

// 3 lý do @Transactional KHÔNG rollback — hay bị hỏi:

// 1. Dùng Checked Exception (phải thêm rollbackFor)
@Transactional(rollbackFor = Exception.class)

// 2. Self-invocation — gọi method @Transactional trong cùng class
// Spring AOP không intercept được internal call → tách ra class khác

// 3. Exception bị catch mất trước khi propagate
public void transfer() {
    try { ... } catch (Exception e) { log.error(e); } // BAD: không re-throw!
}
```

### Spring Security Filter Chain

```
Request → SecurityContextPersistenceFilter
        → JwtAuthenticationFilter (custom — parse & validate JWT)
        → ExceptionTranslationFilter (chuyển exception → HTTP response)
        → AuthorizationFilter (kiểm tra quyền)
        → DispatcherServlet → Controller
```

> **401 vs 403:**
> - `401 Unauthorized` → Chưa xác thực (không có token / token hết hạn)
> - `403 Forbidden` → Đã xác thực nhưng KHÔNG CÓ QUYỀN (role không đủ)

### N+1 Query — Phát Hiện & Fix

```properties
# Thêm vào application.properties để phát hiện N+1:
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

```java
// Fix bằng JOIN FETCH:
@Query("SELECT t FROM Transaction t JOIN FETCH t.account WHERE t.id = :id")
Optional<Transaction> findByIdWithAccount(@Param("id") Long id);
```

---

## 🔷 PHẦN III — Database (Trọng Tâm Nhất cho TPBank)

### ACID — Phải Thuộc Lòng

| Chữ | Tính chất | Ví dụ trong chuyển tiền |
|-----|-----------|------------------------|
| **A** — Atomicity | Tất cả hoặc không có gì | Trừ tiền + cộng tiền: cùng thành công hoặc cùng rollback |
| **C** — Consistency | Dữ liệu luôn hợp lệ trước và sau GD | Tổng tiền trong hệ thống không thay đổi sau chuyển tiền |
| **I** — Isolation | Các GD đồng thời không ảnh hưởng nhau | Isolation Level quyết định mức độ cô lập |
| **D** — Durability | GD đã commit tồn tại mãi dù server crash | PostgreSQL dùng WAL (Write-Ahead Log) |

### 4 Isolation Level — TPBank Hỏi Chắc Chắn

| Isolation Level | Ngăn vấn đề gì | Còn vấn đề gì | Dùng khi nào |
|----------------|----------------|----------------|--------------|
| READ UNCOMMITTED | Không ngăn gì | Dirty Read, Non-repeatable, Phantom | ❌ Không dùng trong ngân hàng |
| **READ COMMITTED** | Dirty Read | Non-repeatable Read, Phantom Read | ✅ Default PostgreSQL, GD đơn giản |
| REPEATABLE READ | Dirty + Non-repeatable | Phantom Read | Báo cáo, xem lịch sử nhất quán |
| SERIALIZABLE | Ngăn tất cả | Không còn vấn đề gì | GD tài chính quan trọng |

### 3 Vấn Đề Đọc Hay Gặp

```sql
-- 1. DIRTY READ: Đọc data chưa commit
-- T1: UPDATE account SET balance=0 (chưa commit)
-- T2: SELECT balance → Thấy 0 (SAI! T1 có thể rollback)

-- 2. NON-REPEATABLE READ: Cùng query, khác kết quả
-- T1: SELECT balance → 1000
-- T2: UPDATE balance=500, COMMIT
-- T1: SELECT balance → 500 (Đã thay đổi trong cùng 1 TX!)

-- 3. PHANTOM READ: Thêm row mới làm thay đổi kết quả
-- T1: SELECT COUNT(*) WHERE amount > 1000 → 5
-- T2: INSERT transaction amount=5000, COMMIT
-- T1: SELECT COUNT(*) WHERE amount > 1000 → 6 (Thêm 1 row!)
```

### Index — Khi Nào Tạo, Khi Nào Không

| Trường hợp | Nên làm gì | Lý do |
|-----------|-----------|-------|
| Cột thường WHERE, JOIN | **TẠO INDEX** | Tăng tốc từ O(n) → O(log n) |
| Cột ít giá trị phân biệt (status) | **KHÔNG tạo** | Selectivity thấp, full scan vẫn nhanh hơn |
| Nhiều cột WHERE cùng lúc | **TẠO COMPOSITE INDEX** | Index (col1, col2) nhanh hơn 2 index đơn lẻ |
| Table ít đọc nhiều ghi (log) | **KHÔNG tạo nhiều index** | Mỗi INSERT phải cập nhật tất cả index → chậm |

```sql
-- Kiểm tra query có dùng index không:
EXPLAIN ANALYZE SELECT * FROM transactions
WHERE from_account_id = 123 AND created_at > '2024-01-01';
-- Muốn thấy: "Index Scan" hoặc "Bitmap Index Scan"
-- KHÔNG muốn: "Seq Scan" trên bảng lớn (> 10k row)

-- Tại sao có index mà vẫn chậm?
-- 1. WHERE LOWER(email) = ... → index không dùng được (dùng functional index)
-- 2. WHERE amount::text LIKE '%100%' → Cast phá index
-- 3. Index chưa ANALYZE sau khi thêm nhiều data
```

### Deadlock & Cách Phòng Tránh

```
Vấn đề: Thread A lock account 1 → lock account 2
         Thread B lock account 2 → lock account 1
         → Cả 2 chờ nhau → DEADLOCK

Giải pháp: Luôn lock theo thứ tự ID tăng dần
         Thread A: lock min(1,2)=1 trước, rồi lock max(1,2)=2
         Thread B: cũng lock min(2,1)=1 trước → phải chờ Thread A → KHÔNG deadlock
```

```java
// Trong TransferService:
Long minId = Math.min(request.getFromId(), request.getToId());
Long maxId = Math.max(request.getFromId(), request.getToId());
Account first  = accountRepo.findByIdWithLock(minId).orElseThrow(...);
Account second = accountRepo.findByIdWithLock(maxId).orElseThrow(...);
```

### 🧩 Plan Tích Hợp JTA vào Module Transfer (Dự phòng mở rộng)

> **Bối cảnh hiện tại:** `TransferServiceImpl.transferMoney()` dùng **local transaction** (`@Transactional` của Spring + 1 PostgreSQL datasource). Điều này **đủ và đúng** khi chuyển tiền trong **cùng 1 database**.
> Nếu sau này mở rộng sang **nhiều nguồn tài nguyên** (2 DB khác nhau, hoặc DB + Message Queue), ta cần **JTA (XA Transaction)** để đảm bảo "all-or-nothing" xuyên resource.

#### 1. Khi nào THỰC SỰ cần JTA

| Kịch bản | Có cần JTA (XA)? |
|----------|------------------|
| Chuyển tiền trong 1 DB (hiện tại) | ❌ Local transaction là đủ |
| Chuyển tiền giữa 2 DB khác nhau | ✅ Cần XA |
| DB + ghi Kafka/RabbitMQ (outbox) | ✅ Cần XA (hoặc SAGA) |
| Gọi API ngân hàng đối tác (qua mạng) | ❌ Dùng SAGA, **không** dùng JTA |

#### 2. Các bước tích hợp (Plan chi tiết)

**Bước 1 — Thêm dependency JTA (dùng Atomikos cho app standalone)**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jta-atomikos</artifactId>
</dependency>
```
> Spring Boot sẽ **tự động** cấu hình `JtaTransactionManager` thay cho `DataSourceTransactionManager` khi phát hiện JTA starter.

**Bước 2 — Cấu hình XA DataSource(s) trong `application.yml`**
```yaml
spring:
  jta:
    log-dir: ./jta-logs                 # Recovery log khi TM crash
    transaction-manager-id: tplite-bankcore
  datasource:
    source:                             # DB tài khoản nguồn
      xa:
        properties:
          URL: jdbc:postgresql://localhost:5432/tplite_db
          user: postgres
          password: 123456
    target:                             # DB tài khoản đích / ledger
      xa:
        properties:
          URL: jdbc:postgresql://localhost:5432/tplite_ledger_db
          user: postgres
          password: 123456
```

**Bước 3 — Khai báo XADataSource + EntityManagerFactory dùng JTA**
```java
@Configuration
public class JtaConfig {
    @Bean
    public DataSource sourceDataSource() {
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
        // set URL/user/password ...
        return ds; // XA-capable connection
    }

    // EntityManagerFactory cho mỗi datasource phải trỏ về JtaTransactionManager
    // (Spring Boot auto-config nếu đặt tên chuẩn: sourceEntityManagerFactory, ...)
}
```

**Bước 4 — Sửa `TransferService` (giữ nguyên annotation, đổi nền tảng)**
```java
@Override
@Transactional(rollbackFor = Exception.class) // Giờ là JTA/XA transaction
public TransferDto transferMoney(TransferDto request) {
    // Lock order + kiểm tra số dư + business logic GIỮ NGUYÊN
    // Chỉ khác: transaction giờ quản lý 2 XA datasource
    // Nếu 1 trong 2 DB lỗi → cả 2 rollback (qua 2PC)
}
```

#### 3. Trade-off cần biết (phỏng vấn hay hỏi)

- **2PC (Two-Phase Commit)** chậm hơn local transaction (có thêm phase *prepare*).
- Resource bị khóa lâu hơn → throughput giảm, nguy cơ contention tăng.
- Nếu Transaction Manager crash giữa chừng → cần **recovery log** (Atomikos ghi vào `jta-logs`).
- **Alternative:** **SAGA pattern** (choreography qua event / orchestration qua coordinator) — không khóa resource, nhưng bắt buộc phải có **compensation** (hoàn tác) khi 1 bước thất bại.

#### 4. Quyết định đề xuất

> Với dự án BankCore hiện tại (1 DB duy nhất), **KHÔNG cần JTA** — local transaction + Pessimistic Lock đã đảm bảo ACID và chống deadlock tốt.
> Chỉ tích hợp JTA khi có kịch bản **đa nguồn tài nguyên cùng DB** thực tế. Nếu gọi hệ thống bên ngoài (API đối tác, thanh toán quốc tế), ưu tiên **SAGA** thay vì JTA vì không thể rollback được remote call.

---

## 🔷 PHẦN IV — MSA Cơ Bản (Hiểu Khái Niệm Là Đủ — KHÔNG cần áp dụng thực tế)

> ⚠️ **Lưu ý cho mục tiêu Junior:** Phần này **chỉ cần hiểu khái niệm** để trả lời phỏng vấn. KHÔNG cần code MSA, không cần tách service, không cần Docker/K8s phức tạp. Tập trung thời gian vào **Java Core + Spring + Database** (trọng tâm TPBank). Chỉ làm sâu hơn nếu có hứng thú, không bắt buộc.

| Câu hỏi phỏng vấn | Trả lời ngắn gọn |
|-------------------|------------------|
| MSA là gì? | Kiến trúc chia hệ thống thành các service nhỏ độc lập, giao tiếp qua API hoặc Message Queue |
| MSA vs Monolith? | Monolith: 1 app, dễ dev/debug, khó scale riêng. MSA: nhiều app, phức tạp hơn nhưng scale linh hoạt |
| API Gateway làm gì? | Điểm vào duy nhất: authentication, routing, rate limiting, load balancing, logging |
| Distributed Transaction? | SAGA pattern: Choreography (event) hoặc Orchestration (coordinator) — Junior biết khái niệm là đủ |

---

## 🔷 PHẦN V — DSA Song Song (30–45 phút/ngày)

| Tuần | Pattern | Bài tiêu biểu | Áp dụng trong ngân hàng |
|------|---------|---------------|------------------------|
| T1–2 | Array / Two Pointer / Sliding Window | Two Sum, Maximum Subarray | Tìm GD trong khoảng, tính tổng tiền |
| T3–4 | HashMap & HashSet | Group Anagrams, Top K Frequent | Đếm GD theo loại, tìm duplicate refCode |
| T5–6 | Stack & Queue | Valid Parentheses, BFS | Xử lý lệnh chờ, kiểm tra biểu thức |
| T7 | Binary Search | Search in Rotated Array | Tìm nhanh GD trong log đã sắp xếp |
| T8 | Tree cơ bản (BFS/DFS) | Level Order Traversal | Hiểu cây index B-Tree trong DB |

> **Chiến lược:** 1 Easy (warm-up) + 2 Medium cùng pattern mỗi ngày.  
> Nếu 20 phút không ra → xem hint. 40 phút vẫn không ra → xem giải, hiểu, tự code lại.

---

## 🔷 PHẦN VI — Dự Án BankCore (Demo Phỏng Vấn)

### Tech Stack

| Tầng | Công nghệ | Phiên bản |
|------|-----------|----------|
| Runtime | Java | 21 (LTS) |
| Framework | Spring Boot | 3.2+ |
| ORM | Spring Data JPA + Hibernate | 6.x |
| Database | PostgreSQL | 15+ |
| Cache | Redis | 7+ |
| Security | Spring Security + JWT | 6.x |
| Migration | Flyway | 9+ |
| Container | Docker + Compose | Latest |
| CI/CD | GitHub Actions | Latest |
| Doc | Swagger / OpenAPI 3 | 2.x |

### 8 Module Chức Năng

| Module | Chức năng | Kiến thức áp dụng |
|--------|-----------|------------------|
| 1. Auth & User | Đăng ký, đăng nhập, 2FA OTP | JWT, BCrypt, Spring Security |
| 2. Account | Mở tài khoản, xem số dư, đóng băng | JPA, @Version Optimistic Lock |
| **3. Transfer** ⭐ | Chuyển tiền, kiểm tra hạn mức | **ACID, Pessimistic Lock, chống Deadlock** |
| 4. Transaction History | Lịch sử GD, filter, pagination | JPA Specification, Index tối ưu |
| 5. Card Management | Phát hành thẻ, khóa thẻ | State Machine, @Transactional |
| 6. Loan | Đăng ký vay, tính lãi suất | Business logic, Schedule job |
| 7. Notification | Thông báo email/SMS | @Async, Queue nội bộ |
| 8. Admin Dashboard | Quản lý user, audit log | Spring AOP, RBAC |

### Script Trình Bày Dự Án (3 phút)

```
1. CONTEXT (20 giây):
   "Em xây dựng hệ thống ngân hàng cơ bản tên BankCore, bao gồm quản lý tài khoản,
    chuyển tiền, lịch sử giao dịch và phát hành thẻ."

2. TECH STACK (30 giây):
   "Backend dùng Java 21 + Spring Boot 3, PostgreSQL vì cần ACID cho giao dịch tài chính,
    Redis cho cache và lưu OTP."

3. ĐIỂM KHÓ NHẤT & CÁCH GIẢI QUYẾT (90 giây — QUAN TRỌNG NHẤT):
   "Phần khó nhất là xử lý chuyển tiền đồng thời. Em gặp Race Condition khi
    2 user cùng rút tiền lúc đó. Em đã học sâu về Optimistic Lock với @Version
    và Pessimistic Lock với SELECT FOR UPDATE.
    Em chọn Pessimistic Lock cho chuyển tiền vì cần đảm bảo tuyệt đối,
    và lock 2 account theo thứ tự ID tăng dần để tránh Deadlock."

4. KẾT QUẢ (30 giây):
   "Dự án có Swagger docs, Unit test 70%+ coverage Service layer,
    deploy bằng Docker Compose, CI/CD với GitHub Actions."
```

---

## 📝 30 Câu Hỏi Phỏng Vấn TPBank Hay Gặp Nhất

### Nhóm Java Core (10 câu)
1. 4 tính chất OOP là gì? Ví dụ mỗi tính chất trong hệ thống ngân hàng?
2. HashMap hoạt động như thế nào? `hashCode()` và `equals()` liên quan gì?
3. Checked Exception vs Unchecked Exception — khi nào dùng cái nào?
4. `==` vs `equals()` — sự khác biệt? Viết ví dụ bị lỗi vì dùng sai?
5. String là immutable — tại sao? StringBuilder khi nào dùng?
6. Concurrency: `synchronized` vs `ReentrantLock` — khác nhau?
7. Stream API: `filter` vs `map` vs `flatMap` — khi nào dùng cái nào?
8. `Optional` là gì? Tại sao dùng thay vì kiểm tra null?
9. `interface` vs `abstract class` — khi nào dùng cái nào?
10. Garbage Collection hoạt động như thế nào? Khi nào xảy ra?

### Nhóm Spring Boot (10 câu)
11. IoC và DI là gì? Spring quản lý Bean như thế nào?
12. Bean Scope: Singleton vs Prototype? Singleton an toàn đa luồng không?
13. `@Transactional` hoạt động như thế nào? Khi nào KHÔNG rollback?
14. JPA Lazy vs Eager loading — khác nhau, khi nào dùng? LazyInitException là gì?
15. N+1 query là gì? Cách phát hiện và fix?
16. Spring Security Filter Chain — request đi qua những filter nào?
17. 401 vs 403 — khác nhau? Spring Security xử lý thế nào?
18. `@ControllerAdvice` làm gì? Tại sao cần GlobalExceptionHandler?
19. Constructor injection vs `@Autowired` — cái nào tốt hơn và tại sao?
20. Spring Boot Auto-configuration hoạt động như thế nào?

### Nhóm Database (10 câu)
21. ACID là gì? Giải thích từng chữ cái với ví dụ trong chuyển tiền?
22. 4 Isolation Level là gì? Mỗi level ngăn vấn đề gì?
23. Dirty Read, Non-repeatable Read, Phantom Read — là gì, khác nhau?
24. Index là gì? Khi nào tạo, khi nào KHÔNG tạo?
25. Composite index là gì? Thứ tự cột quan trọng như thế nào?
26. Optimistic Lock vs Pessimistic Lock — khi nào dùng cái nào?
27. Deadlock là gì? Cách phòng tránh trong hệ thống ngân hàng?
28. `EXPLAIN ANALYZE` đọc kết quả như thế nào? Seq Scan vs Index Scan?
29. Foreign Key constraint có ảnh hưởng performance không?
30. SQL Transaction: BEGIN/COMMIT/ROLLBACK/SAVEPOINT — mỗi thứ dùng khi nào?

---

## ✅ Checklist Sẵn Sàng Đi Phỏng Vấn TPBank

- [ ] Giải thích ACID với ví dụ cụ thể trong 2 phút không nhìn tài liệu
- [ ] Giải thích 4 Isolation Level và 3 vấn đề (Dirty/Non-repeatable/Phantom Read)
- [ ] Code `TransferService` với `@Transactional` + Lock từ đầu không copy
- [ ] Implement JWT + Spring Security Filter Chain từ đầu
- [ ] Fix N+1 query bằng JOIN FETCH (kiểm tra bằng `show-sql=true`)
- [ ] Trình bày dự án BankCore mạch lạc trong 3–5 phút
- [ ] `EXPLAIN ANALYZE` và đọc được Seq Scan vs Index Scan
- [ ] Giải thích Deadlock và cách phòng tránh bằng lock order
- [ ] `docker compose up` là chạy được cả hệ thống
- [ ] Làm được > 80% bài LeetCode Easy các pattern đã học

---

## 🤖 Prompt Hỏi AI Hiệu Quả

| Mục tiêu | Prompt sử dụng |
|----------|----------------|
| Mock phỏng vấn Java | _"Hãy đóng vai phỏng vấn viên TPBank, hỏi tôi 10 câu về Java Core theo thứ tự từ dễ đến khó. Hỏi từng câu, cho tôi trả lời xong mới nhận xét."_ |
| Kiểm tra ACID | _"Tôi sẽ giải thích ACID với ví dụ chuyển tiền, hãy sửa nếu sai: [trả lời của bạn]"_ |
| Review code live | _"Đây là code TransferService của tôi. Hãy review như senior: 1) Bug gì? 2) Vấn đề performance? 3) Vấn đề concurrency? 4) Cách viết clean hơn?"_ |
| Tập trình bày dự án | _"Tôi sẽ pitch dự án BankCore trong 3 phút. Ghi lại những gì tôi nói và nhận xét."_ |
| Kiểm tra Isolation Level | _"Hãy hỏi tôi các câu hỏi về Isolation Level như phỏng vấn, từ đơn đến khó, cho tôi trả lời từng câu."_ |

---

> 💡 **Lời khuyên từ Senior:** _"Ôn chắc core, hiểu sâu hơn biết nhiều mà rộng — đó là bí quyết."_  
> 🎯 **Core chắc + Project thực tế + DSA = Pass Junior TPBank**
