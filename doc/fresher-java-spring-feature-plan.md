# Plan lam nhanh module de ung tuyen Java Spring Fresher

Muc tieu ngan han: co project Spring Boot chay duoc, co API ro rang, co database, co security JWT, co CRUD nghiep vu va biet giai thich luong code khi phong van.

## 1. Security/Auth - uu tien cao nhat

Da lam:
- Register user, hash password bang BCrypt.
- Gan role mac dinh `CUSTOMER`.
- Login bang `AuthenticationManager`.
- Sinh JWT access token.
- Sinh refresh token luu DB.
- Refresh token de lay access token moi.
- Logout bang cach revoke refresh token.
- Load role/permission tu DB qua `CustomUserDetailsService`.
- Gan JWT filter truoc `UsernamePasswordAuthenticationFilter`.
- Chuan hoa loi security: 401 khi chua login/sai token, 403 khi khong du quyen.
- Them `GlobalExceptionHandler` cho validation, duplicate, business error.
- Them endpoint test RBAC tai `/api/security-test/**`.

Can on de phong van:
- Vi sao password phai hash, khong ma hoa 2 chieu.
- Vi sao JWT phu hop REST API stateless.
- `UserDetailsService` la cau noi giua DB user va Spring Security.
- `SecurityContext` luu `Authentication` cua request hien tai.
- `hasRole` dung `ROLE_...`, `hasAuthority` dung permission truc tiep.

OAuth2/Google:
- Chua can lam ngay cho JD nay.
- Neu lam Google login thi moi can Google Cloud Console de tao OAuth Client ID/Secret.

## 2. User/Customer CRUD

Muc tieu:
- CRUD customer/user profile.
- Validate input bang `jakarta.validation`.
- Tim kiem theo email, phone, status.
- Phan quyen: user chi xem sua profile cua minh, admin/staff xem danh sach.

Nen co API:
- `GET /api/me`
- `PUT /api/me`
- `GET /api/admin/users`
- `PATCH /api/admin/users/{id}/status`

Kien thuc lien he:
- OOP: Entity, DTO, Service, Repository tach rieng.
- Collection: dung `List` cho danh sach ket qua, `Set` cho role/permission khong trung.
- Transaction: can cho update profile/status.

## 3. Account module

Muc tieu:
- Tao tai khoan ngan hang basic.
- Xem danh sach tai khoan cua user.
- Khoa/mo tai khoan.
- Xem so du.

Nen co API:
- `POST /api/accounts`
- `GET /api/accounts/my`
- `GET /api/accounts/{id}`
- `PATCH /api/accounts/{id}/status`

Kien thuc lien he:
- ACID khi tao tai khoan.
- Unique constraint cho account number.
- Enum status: ACTIVE, LOCKED, CLOSED.

## 4. Transfer/Transaction module

Muc tieu:
- Chuyen tien noi bo basic.
- Tru tien tai khoan nguon, cong tien tai khoan dich.
- Luu transaction/audit.

Nen co API:
- `POST /api/transfers`
- `GET /api/transactions/my`
- `GET /api/transfers/{id}`

Kien thuc lien he:
- `@Transactional` bat buoc vi co nhieu thao tac DB.
- ACID: neu tru tien thanh cong nhung cong tien that bai thi rollback.
- Can check so du, account status, amount > 0.
- Sau nay co the them optimistic/pessimistic locking.

## 5. Loan module - basic

Muc tieu:
- Customer tao ho so vay.
- Staff review.
- Admin/manager approve/reject.

Nen co API:
- `POST /api/loans`
- `GET /api/loans/my`
- `GET /api/admin/loans`
- `PATCH /api/admin/loans/{id}/review`

Kien thuc lien he:
- Workflow status: PENDING, REVIEWING, APPROVED, REJECTED.
- RBAC: CUSTOMER tao, STAFF review, ADMIN approve.

## 6. Notification module - basic

Muc tieu:
- Luu thong bao trong DB sau register, transfer, loan status change.
- Chua can Kafka.

Nen co API:
- `GET /api/notifications/my`
- `PATCH /api/notifications/{id}/read`

Kien thuc lien he:
- Sau nay neu muon async thi moi them Kafka/message queue.
- Hien tai dung service call truc tiep la du cho fresher.

## 7. Error handling va API response

Muc tieu:
- Co `GlobalExceptionHandler`.
- Loi validate tra 400.
- Sai login tra 401.
- Khong co quyen tra 403.
- Resource khong ton tai tra 404.

Kien thuc lien he:
- Tra loi phong van ve trai nghiem nguoi dung va debug.
- Khong tra stack trace ra client.

## Thu tu lam de kip

1. Hoan thien security/auth.
2. Them global exception handler.
3. Lam user/customer profile.
4. Lam account.
5. Lam transfer + transaction.
6. Lam loan basic.
7. Lam notification DB basic.
8. Viet README: cach chay, API mau, tai khoan test, luong nghiep vu.
