# AGENT.MD

Huong dan cho AI/agent khi code trong project `Bank_Core`.

Muc tieu: code de hieu, de test, de mo rong, bam sat nghiep vu banking co ban, khong lam phuc tap qua muc can thiet.

## 1. Nguyen tac lam viec

- Doc code hien co truoc khi sua.
- Bam theo style, package, cach dat ten dang co trong project.
- Khong refactor lon neu task chi yeu cau sua nho.
- Khong xoa/sua thay doi cua user neu khong duoc yeu cau.
- Uu tien code chay duoc, ro nghiep vu, de giai thich khi phong van.
- Moi thay doi nen co ly do nghiep vu hoac ky thuat ro rang.

## 2. Kien truc module

Moi module nen di theo cau truc don gian:

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

Luong chuan:

```text
Controller -> Service -> Repository -> Database
```

Quy tac:

- Controller chi nhan request, goi service, tra response.
- Service chua nghiep vu chinh.
- Repository chi truy van database.
- DTO dung cho request/response.
- Entity chi map database, khong nhoi logic API.

## 3. SOLID

### Single Responsibility

Mot class chi nen co mot trach nhiem chinh.

Vi du:

- `AuthController`: API login/register.
- `AuthService`: xu ly login/register.
- `JwtService`: tao va validate JWT.
- `UserRepository`: truy van user.

Khong de controller vua query DB, vua hash password, vua tao token.

### Open/Closed

Code nen de mo rong ma khong phai sua nhieu noi.

Vi du:

- Them role `STAFF`, `ADMIN`, `CUSTOMER` thi khong sua lung tung o nhieu API.
- Dung enum/constant thay vi hard-code chuoi rai rac.

### Liskov Substitution

Implementation phai dung contract cua interface.

Vi du neu co `TransferService`, class `TransferServiceImpl` phai thuc hien dung y nghia method da khai bao.

### Interface Segregation

Khong tao interface qua to.

Khong tao `BankingService` gom auth, account, transfer, loan. Nen tach:

- `AuthService`
- `AccountService`
- `TransferService`
- `LoanService`

### Dependency Inversion

- Dung constructor injection.
- Khong dung field injection neu khong can.
- Khong tu `new Service()` hoac `new Repository()` trong service.

## 4. Design patterns nen dung

### DTO Pattern

Dung DTO de tach API data khoi entity.

Vi du:

- `LoginRequest`
- `RegisterRequest`
- `AuthResponse`
- `TransferRequest`

Khong tra thang `User` neu co truong nhay cam nhu `password`.

### Service Layer Pattern

Nghiep vu quan trong phai di qua service:

- Login qua `AuthService`.
- Chuyen tien qua `TransferService`.
- Mo tai khoan qua `AccountService`.
- Tao khoan vay qua `LoanService`.

### Repository Pattern

Repository chi lam viec voi database:

- `findByEmail`
- `existsByEmail`
- `findById`

Khong viet logic tao token, check permission, tinh phi trong repository.

### Strategy Pattern

Dung khi co nhieu cach xu ly cung mot nghiep vu.

Vi du sau nay:

- Tinh phi chuyen tien noi bo.
- Tinh phi chuyen tien lien ngan hang.
- Tinh lai khoan vay theo loai khoan vay.

Chi dung khi code bat dau co nhieu `if/else` cung mot kieu.

### Factory Pattern

Dung khi can tao object theo loai.

Vi du:

- Tao transaction theo `DEPOSIT`, `WITHDRAW`, `TRANSFER`.
- Tao notification theo `EMAIL`, `SMS`, `IN_APP`.

Khong ap dung pattern chi de cho "xịn"; chi dung khi no lam code de hieu hon.

## 5. Nguyen tac Security/Auth

- Password phai hash bang `PasswordEncoder`.
- Khong luu plain text password.
- Login thanh cong moi tra JWT.
- JWT chi nen chua thong tin can thiet: `userId`, `email`, `role`.
- API nhay cam phai yeu cau authentication.
- API quan tri phai check role/permission.
- Khong log password, full token, secret key.
- Secret key khong hard-code trong Java code; nen de trong config/env.
- Phan biet:
  - Authentication: ban la ai?
  - Authorization: ban duoc lam gi?

## 6. RBAC

Role co ban:

- `CUSTOMER`: khach hang.
- `STAFF`: nhan vien.
- `ADMIN`: quan tri.

Phan quyen goi y:

- `CUSTOMER`: xem tai khoan cua minh, chuyen tien, xem lich su giao dich cua minh.
- `STAFF`: xem ho so khach hang, xu ly KYC/loan co ban.
- `ADMIN`: quan ly user, role, cau hinh he thong.

Quy tac quan trong:

- Khong cho user tu truyen `userId` de xem du lieu nguoi khac neu co the lay user tu JWT.
- API nao lien quan tien/phien dang nhap/quyen han phai check user hien tai.

## 7. Nguyen tac banking co ban

- Giao dich tien phai dung database transaction.
- Khong cho so du am neu nghiep vu khong cho phep thau chi.
- Moi giao dich nen co status: `PENDING`, `SUCCESS`, `FAILED`.
- Khong xoa lich su giao dich tuy tien.
- Can audit log cho hanh dong quan trong: login, transfer, lock account, approve loan.
- Transfer phai check:
  - Tai khoan nguon ton tai.
  - Tai khoan dich ton tai.
  - Tai khoan nguon/dich dang active.
  - So du du.
  - So tien hop le.

## 8. Validation va error handling

- Request DTO dung validation annotation:
  - `@NotBlank`
  - `@Email`
  - `@NotNull`
  - `@Positive`
- Khong tra raw exception ra client.
- Dung response format thong nhat neu project da co.
- Message loi nen ngan gon, de hieu:
  - `Email already exists`
  - `Invalid email or password`
  - `Insufficient balance`
  - `Account is locked`

## 9. Transaction

Dung `@Transactional` tai service method co thay doi du lieu quan trong.

Vi du:

- Chuyen tien.
- Tao khoan vay.
- Thanh toan khoan vay.
- Khoa/mo tai khoan.

Khong dat transaction o controller.

## 10. Testing

Uu tien test phan co rui ro:

- Auth login/register.
- JWT generate/validate.
- Transfer du tien/khong du tien.
- Khong cho user xem du lieu cua nguoi khac.
- Loan tinh lich tra no co ban.

Test it nhung dung trong tam tot hon test nhieu ma khong cham vao nghiep vu.

## 11. Quy tac dat ten

- Class: `PascalCase`, vi du `AuthService`, `JwtService`.
- Method/variable: `camelCase`, vi du `findByEmail`, `accessToken`.
- Package: chu thuong, vi du `module.auth.service`.
- Request DTO: `LoginRequest`, `RegisterRequest`.
- Response DTO: `AuthResponse`, `UserResponse`.
- Entity enum nen ro nghia: `UserStatus`, `TransactionStatus`, `TransactionType`.

## 12. Checklist truoc khi xong task

- Code compile duoc.
- API chay duoc neu task co API.
- Co validation request.
- Co xu ly loi co ban.
- Khong lo password/token/secret.
- Controller khong chua nghiep vu nang.
- Repository khong chua nghiep vu.
- Service ro trach nhiem.
- Khong lam phuc tap qua muc can thiet.

