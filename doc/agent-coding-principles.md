# Agent Coding Principles

File này dùng làm nguyên tắc khi làm project `core-banking`. Mục tiêu là code dễ hiểu, dễ test, dễ mở rộng, không làm phức tạp quá mức cần thiết.

## 1. Mục tiêu khi code

- Ưu tiên hiểu nghiệp vụ trước khi code.
- Mỗi module chỉ làm đúng trách nhiệm chính của nó.
- Code đơn giản trước, mở rộng sau.
- Không copy paste logic nghiệp vụ ở nhiều nơi.
- Không đưa logic nghiệp vụ quan trọng vào controller.
- Luôn nghĩ đến lỗi dữ liệu, lỗi quyền truy cập, lỗi giao dịch tiền.

## 2. SOLID

### S - Single Responsibility Principle

Một class chỉ nên có một lý do chính để thay đổi.

Ví dụ:

- `AuthController`: nhận request/response đăng nhập, đăng ký.
- `AuthService`: xử lý nghiệp vụ đăng nhập, đăng ký.
- `JwtService`: tạo và kiểm tra JWT.
- `UserRepository`: truy vấn dữ liệu user.

Không nên để controller vừa validate, vừa query DB, vừa tạo token, vừa encode password.

### O - Open/Closed Principle

Code nên dễ mở rộng mà không phải sửa nhiều logic cũ.

Ví dụ:

- Sau này thêm role `STAFF`, `ADMIN`, `CUSTOMER` thì không sửa lung tung ở nhiều API.
- Dùng enum/permission rõ ràng thay vì hard-code nhiều chuỗi rải rác.

### L - Liskov Substitution Principle

Class con hoặc implementation phải thay thế được interface/base class mà không làm sai hành vi.

Trong project này chỉ cần nhớ: nếu tạo interface như `TransferService`, implementation phải đúng contract đã định nghĩa.

### I - Interface Segregation Principle

Không tạo interface quá to.

Ví dụ không nên có một interface `BankingService` chứa đủ login, transfer, loan, account. Nên tách:

- `AuthService`
- `TransferService`
- `AccountService`
- `LoanService`

### D - Dependency Inversion Principle

Service nên phụ thuộc vào abstraction hoặc component rõ trách nhiệm, không tự tạo dependency bằng `new`.

Trong Spring Boot:

- Dùng constructor injection.
- Không dùng field injection nếu không cần.
- Không tự `new Repository()` hoặc `new Service()` trong code nghiệp vụ.

## 3. Clean Architecture đơn giản cho project

Mỗi module nên đi theo cấu trúc:

```text
module
  auth
    controller
    dto
    service
    service/impl
  user
    entity
    repository
    service
  account
    entity
    repository
    service
  transfer
    controller
    dto
    entity
    repository
    service
```

Luồng chuẩn:

```text
Controller -> Service -> Repository -> Database
```

Quy tắc:

- Controller không chứa nghiệp vụ.
- Service chứa nghiệp vụ.
- Repository chỉ truy vấn dữ liệu.
- DTO dùng cho request/response, không trả entity trực tiếp nếu dữ liệu nhạy cảm.
- Entity dùng để map database, không nhồi logic API vào entity.

## 4. Design Pattern nên dùng

### DTO Pattern

Dùng DTO để tách dữ liệu API khỏi entity.

Ví dụ:

- `LoginRequest`
- `RegisterRequest`
- `AuthResponse`
- `TransferRequest`

Không trả thẳng `User` vì có thể lộ `password`.

### Service Layer Pattern

Mọi nghiệp vụ quan trọng đi qua service.

Ví dụ:

- Login phải qua `AuthService`.
- Chuyển tiền phải qua `TransferService`.
- Mở tài khoản phải qua `AccountService`.

### Repository Pattern

Repository chỉ làm việc với database.

Ví dụ:

- `findByEmail`
- `existsByEmail`
- `findById`

Không viết nghiệp vụ như kiểm tra quyền hay tạo token trong repository.

### Strategy Pattern

Dùng khi có nhiều cách xử lý cùng một nghiệp vụ.

Ví dụ sau này:

- Tính phí chuyển tiền nội bộ.
- Tính phí chuyển tiền liên ngân hàng.
- Tính lãi khoản vay theo loại khoản vay.

Ban đầu chưa cần áp dụng nếu code còn đơn giản.

### Factory Pattern

Dùng khi cần tạo object theo loại.

Ví dụ sau này:

- Tạo transaction theo `DEPOSIT`, `WITHDRAW`, `TRANSFER`.
- Tạo notification theo `EMAIL`, `SMS`, `IN_APP`.

Ban đầu chỉ dùng khi thấy `if/else` tạo object bắt đầu dài.

## 5. Nguyên tắc Security/Auth

- Password phải được hash bằng `PasswordEncoder`, không lưu plain text.
- Login thành công mới trả JWT.
- JWT chỉ nên chứa thông tin cần thiết như `userId`, `email`, `role`.
- API nhạy cảm phải yêu cầu authentication.
- API quản trị phải kiểm tra role/permission.
- Không log password, token đầy đủ, secret key.
- Secret key không hard-code trong code Java; đặt trong config/env.
- Phân biệt rõ:
  - Authentication: bạn là ai?
  - Authorization: bạn được làm gì?

## 6. Nguyên tắc RBAC

Role cơ bản:

- `CUSTOMER`: khách hàng.
- `STAFF`: nhân viên.
- `ADMIN`: quản trị.

Ví dụ phân quyền:

- `CUSTOMER`: xem tài khoản của mình, chuyển tiền, xem lịch sử giao dịch của mình.
- `STAFF`: xem hồ sơ khách hàng, hỗ trợ xử lý loan/KYC.
- `ADMIN`: quản lý user, role, cấu hình hệ thống.

Không cho user tự truyền `userId` để xem dữ liệu người khác nếu API lấy được user hiện tại từ token.

## 7. Nguyên tắc nghiệp vụ ngân hàng cơ bản

- Giao dịch tiền phải dùng transaction database.
- Không cho số dư âm nếu nghiệp vụ không cho phép thấu chi.
- Mọi giao dịch nên có trạng thái: `PENDING`, `SUCCESS`, `FAILED`.
- Lưu lịch sử giao dịch, không xóa giao dịch tùy tiện.
- Cần audit log cho hành động quan trọng: login, transfer, lock account, approve loan.
- Với transfer: kiểm tra tài khoản nguồn, tài khoản đích, số dư, trạng thái tài khoản.

## 8. Validation và Error Handling

- Request DTO dùng annotation validation: `@NotBlank`, `@Email`, `@NotNull`, `@Positive`.
- Không để lỗi hệ thống raw trả thẳng ra client.
- Dùng response format thống nhất.
- Message lỗi nên dễ hiểu, ví dụ:
  - `Email already exists`
  - `Invalid email or password`
  - `Insufficient balance`
  - `Account is locked`

## 9. Transaction

Dùng `@Transactional` ở service method có thay đổi dữ liệu quan trọng.

Ví dụ:

- Chuyển tiền.
- Tạo khoản vay.
- Thanh toán khoản vay.
- Khóa/mở tài khoản.

Không đặt transaction ở controller.

## 10. Testing

Ưu tiên test các phần có rủi ro:

- Auth login/register.
- JWT generate/validate.
- Transfer đủ tiền/không đủ tiền.
- Không cho user xem dữ liệu của người khác.
- Loan tính lịch trả nợ cơ bản.

Test ít nhưng đúng trọng tâm tốt hơn test nhiều mà không kiểm tra nghiệp vụ.

## 11. Quy tắc đặt tên

- Class: `PascalCase`, ví dụ `AuthService`, `JwtService`.
- Method/variable: `camelCase`, ví dụ `findByEmail`, `accessToken`.
- Package: chữ thường, ví dụ `module.auth.service`.
- DTO request: `LoginRequest`, `RegisterRequest`.
- DTO response: `AuthResponse`, `UserResponse`.

## 12. Khi nào dùng design pattern?

Không áp dụng pattern chỉ để cho "xịn".

Chỉ dùng khi:

- Code có nhiều `if/else` cùng một kiểu xử lý.
- Có nhiều loại nghiệp vụ tương tự nhau.
- Logic bắt đầu lặp lại.
- Module cần mở rộng thường xuyên.

Nếu nghiệp vụ còn nhỏ, service method rõ ràng là đủ.

## 13. Checklist trước khi hoàn thành một module

- API chạy được.
- Có validation request.
- Có xử lý lỗi cơ bản.
- Không lộ password/token/secret.
- Service không quá dài và không ôm nhiều trách nhiệm.
- Repository không chứa nghiệp vụ.
- Có compile/test cơ bản.
- Có ghi chú nghiệp vụ nếu logic dễ nhầm.

