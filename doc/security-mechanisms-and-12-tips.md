# Security Mechanisms And 12 API Security Tips

File nay tom tat 4 co che bao mat trong hinh va 12 tip bao mat API nen nho khi lam backend Spring Boot.

## 1. Bon co che bao mat trong hinh

### 1. OAuth Tokens

OAuth token dung de cap quyen truy cap tai nguyen ma khong can dua password cho moi API.

Vi du:

```text
Client -> xin access token -> Authorization Server
Client -> goi API kem Bearer Token -> Resource Server
Resource Server -> validate token -> tra response
```

Trong request API thuong se co header:

```text
Authorization: Bearer <access_token>
```

Y nghia:

- `access token`: token ngan han de goi API.
- `refresh token`: token dai han hon, dung de xin access token moi.
- `Authorization Server`: noi login/cap token.
- `Resource Server`: API server dang bao ve tai nguyen.

Trong project Spring Boot hien tai, ban co the bat dau bang JWT login/register truoc. OAuth2 nen hoc o muc khai niem truoc, sau do moi lam sau.

### 2. HTTPS / SSL / TLS Certificates

Trong hinh ghi SSL Certificates, nhung thuc te hien nay minh hay goi la HTTPS/TLS. SSL la ten cu, TLS la phien ban hien dai hon.

HTTPS giup:

- Ma hoa du lieu tren duong truyen.
- Xac minh server dung la domain minh dang truy cap.
- Chong nghe len du lieu giua client va server.

Luong don gian:

```text
Client truy cap https://domain.com
Server gui certificate
Client kiem tra certificate:
  - con han khong?
  - duoc CA tin cay cap khong?
  - domain co khop khong?
Neu hop le -> tao session key -> ma hoa du lieu
```

Can nho:

- HTTPS bao ve du lieu tren duong truyen.
- HTTPS khong thay the login/JWT/RBAC.
- Website/API that nen luon dung HTTPS, khong dung HTTP plain text.

### 3. Credentials

Credentials nghia rong la thong tin dung de chung minh danh tinh.

Credentials khong chi dung cho AWS.

Vi du credentials:

- Username/password.
- API key.
- Client ID/client secret.
- AWS access key ID + AWS secret access key.
- Database username/password.
- SSH private key cung co the xem la mot loai credential bi mat.

AWS credentials chi la mot truong hop cu the:

```text
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_SESSION_TOKEN
```

Trong hinh credentials dang noi ve username/password:

```text
Client nhap username/password
Du lieu di qua HTTPS
Server nhan du lieu
Server lookup username
Server verify password da hash trong database
Dung -> authenticated
Sai -> unauthorized
```

Nguyen tac:

- Khong luu password plain text.
- Phai hash password bang BCrypt/PasswordEncoder.
- Khong commit credentials len Git.
- Khong log password, secret, token.

### 4. SSH Keys

SSH key dung nhieu khi login server, deploy code, ket noi GitHub/GitLab, hoac quan tri may chu.

SSH key gom:

- Public key: dua cho server.
- Private key: giu bi mat tren may client.

Luong don gian:

```text
Client co private key
Server luu public key
Client ket noi SSH
Server tao challenge
Client dung private key de ky challenge
Server dung public key de verify
Dung -> cho login
Sai -> tu choi
```

Can nho:

- Khong gui private key qua network.
- Khong share private key.
- Nen dat passphrase cho private key quan trong.
- Server chi can public key de xac minh.

## 2. So sanh nhanh

| Co che | Dung de lam gi | Vi du |
| --- | --- | --- |
| HTTPS/TLS | Ma hoa du lieu tren duong truyen va xac minh server | Truy cap API bang `https://` |
| Credentials | Chung minh danh tinh ban dau | Username/password, API key, AWS key |
| OAuth/JWT Token | Goi API sau khi da login/cap quyen | `Authorization: Bearer <token>` |
| SSH Keys | Dang nhap/quan tri server hoac Git bang public/private key | SSH vao VPS, push GitHub |

## 3. Lien he voi project Spring Boot

Khi lam module Security/Auth co ban:

```text
Register -> hash password -> save user
Login -> verify password -> generate JWT
Request API -> Bearer JWT -> JwtFilter validate -> Spring Security set current user
RBAC -> check role/permission -> allow/deny
```

Thu tu nen hoc:

1. Credentials: email/password.
2. Password hashing: BCrypt.
3. JWT access token.
4. Refresh token.
5. RBAC role/permission.
6. OAuth2 concept.
7. HTTPS deployment concept.
8. SSH key concept cho deploy/server/git.

## 4. 12 API Security Tips

### 1. Use HTTPS

Tat ca API that nen chay qua HTTPS.

Khong gui password, token, banking data qua HTTP plain text.

### 2. Use OAuth2

Dung OAuth2 khi he thong can uy quyen cho app ben thu ba hoac dang nhap qua nha cung cap khac.

Vi du:

- Login with Google.
- App A duoc phep doc data tu app B ma khong biet password user.

Voi project hoc co ban, co the bat dau bang JWT login/register truoc.

### 3. Use WebAuthn

WebAuthn dung cho dang nhap manh hon password, vi du passkey, van tay, Face ID, hardware key.

Dung khi can bao mat cao.

Voi project hoc co ban, chi can hieu khai niem.

### 4. Use Leveled API Keys

API key nen co muc quyen ro rang, khong phai key nao cung full access.

Vi du:

- Key chi doc du lieu.
- Key duoc tao transaction.
- Key chi dung trong moi truong dev.
- Key production co han muc va audit rieng.

### 5. Authorization

Authentication chi noi user la ai. Authorization moi noi user duoc lam gi.

Vi du:

- CUSTOMER chi xem account cua minh.
- STAFF xem ho so khach hang.
- ADMIN quan ly role/user.

Khong duoc chi check login ma bo qua check quyen.

### 6. Rate Limiting

Gioi han so request theo IP/user/action de chong spam, brute force, abuse.

Vi du:

- Login sai qua 5 lan/phut thi khoa tam.
- Transfer API gioi han theo user.
- Public API gioi han theo IP.

### 7. API Versioning

Nen version API de tranh pha vo client cu.

Vi du:

```text
GET /api/v1/users/123
GET /api/v2/users/123
```

Khong nen thay doi response cu mot cach tuy tien neu client dang dung.

### 8. Whitelisting

Chi cho phep nhung IP/user/client/action hop le.

Vi du:

- Admin API chi cho phep tu IP noi bo.
- Webhook chi nhan tu IP cua doi tac.
- Internal service chi cho phep service da dang ky.

### 9. Check OWASP API Security Risks

Nen biet cac nhom loi API pho bien:

- Broken Object Level Authorization.
- Broken Authentication.
- Broken Function Level Authorization.
- Unrestricted Resource Consumption.
- Sensitive data exposure.
- Security misconfiguration.

Trong project banking, loi nguy hiem nhat thuong la user xem/sua du lieu cua user khac.

### 10. Use API Gateway

API Gateway dung de gom cac viec chung:

- Routing.
- Authentication/authorization co ban.
- Rate limiting.
- Logging.
- Request validation co ban.

Voi monolith hoc tap thi chua can API Gateway. Chi can hieu no hay dung trong microservices.

### 11. Error Handling

Loi tra ra client nen ro rang nhung khong lo thong tin noi bo.

Nen:

```text
401 Invalid email or password
403 Access denied
400 Invalid request
```

Khong nen:

```text
NullPointerException at AuthService.java:42
SQL syntax error...
JWT secret is abc...
```

### 12. Input Validation

Validate moi request dau vao.

Vi du:

- Email phai dung format.
- Amount phai > 0.
- Password khong rong.
- Account ID phai hop le.
- Khong tin du lieu client gui len.

Trong Spring Boot dung:

```java
@NotBlank
@Email
@NotNull
@Positive
```

## 5. Hai mo hinh phan quyen hay gap

### 1. RBAC - Role Based Access Control

RBAC la phan quyen theo role.

Tu duy:

```text
User -> Role -> Permission
```

Vi du:

```text
James -> READER -> READ
Isac -> EDITOR -> READ, WRITE
Lee -> ADMIN -> READ, WRITE, ADD, DELETE
```

Bang du lieu thuong gap:

```text
users
roles
permissions
user_has_role
role_has_permission
```

Y nghia:

- User khong nhan permission truc tiep.
- User duoc gan role.
- Role gom nhieu permission.
- Muon doi quyen cua nhieu user thi sua role, khong sua tung user.

Vi du banking:

```text
CUSTOMER:
  - ACCOUNT_READ_OWN
  - TRANSFER_CREATE_OWN
  - TRANSACTION_READ_OWN

STAFF:
  - CUSTOMER_READ
  - KYC_REVIEW
  - LOAN_REVIEW

ADMIN:
  - USER_MANAGE
  - ROLE_MANAGE
  - SYSTEM_CONFIG
```

Uu diem:

- De hieu.
- De code trong Spring Security.
- Hop voi he thong business nhu banking, CRM, admin portal.
- De giai thich khi phong van.

Nhuoc diem:

- Neu quyen qua chi tiet, role co the bi no ra rat nhieu.
- Khong linh hoat bang ACL/policy trong cac he thong tai nguyen lon.

Trong project nay, nen bat dau bang RBAC truoc.

### 2. ACL / Permission Based / Policy Based Access Control

ACL la Access Control List. No phan quyen truc tiep tren user/group/role voi tung resource hoac tung action.

Tu duy:

```text
Subject -> Action -> Resource -> Allow/Deny
```

Trong do:

- Subject: user, group, role, service account.
- Action: read, write, add, delete, approve, transfer.
- Resource: file, folder, S3 bucket, account, loan, connector, job.
- Effect: allow hoac deny.

Vi du ACL:

```text
James can READ file A
Peter can READ file A
Isac can READ and WRITE file A
Lee can READ, WRITE, ADD, DELETE file A
```

Mo hinh nay duoc dung nhieu trong:

- Linux file permission.
- AWS IAM policy.
- S3 bucket policy.
- Database permission.
- Object storage.
- He thong co nhieu resource can gan quyen rieng.

### Linux permission la dang nao?

Linux co permission theo user/group/others:

```text
owner: read/write/execute
group: read/write/execute
others: read/write/execute
```

Vi du:

```text
chmod 755 app.sh
```

Y nghia:

```text
owner: read + write + execute
group: read + execute
others: read + execute
```

Linux co the dung ACL nang cao hon bang `setfacl`, cho phep gan quyen chi tiet cho user/group cu the.

### AWS IAM la dang nao?

AWS IAM gan permission bang policy.

Tu duy:

```text
Principal -> Action -> Resource -> Effect
```

Vi du policy y tuong:

```json
{
  "Effect": "Allow",
  "Action": "s3:GetObject",
  "Resource": "arn:aws:s3:::my-bucket/*"
}
```

Y nghia:

- Ai duoc phep?
- Duoc lam action nao?
- Tren resource nao?
- Ket qua la Allow hay Deny?

AWS co the gan policy cho:

- User.
- Group.
- Role.
- Service.

Day la ly do ban thay no giong ACL/policy-permission hon RBAC co ban.

### RBAC vs ACL

| Tieu chi | RBAC | ACL / Policy |
| --- | --- | --- |
| Tu duy | User co role, role co permission | Ai duoc lam gi tren resource nao |
| De hieu | De hon | Chi tiet hon |
| Do linh hoat | Vua phai | Cao |
| Do phuc tap | Thap/vua | Cao hon |
| Dung trong | App business, admin system | Linux, AWS, storage, resource-level security |
| Vi du | CUSTOMER, STAFF, ADMIN | James READ file A, role X can s3:GetObject bucket Y |

### Nen dung cai nao trong project banking?

Voi project hoc Spring Boot banking:

1. Bat dau bang RBAC:

```text
User -> Role -> Permission
```

2. Sau do them resource ownership:

```text
CUSTOMER chi duoc xem account cua chinh minh
CUSTOMER chi duoc xem transaction cua chinh minh
```

3. Neu can nang cao, moi them ACL/policy:

```text
Staff A duoc review loan cua branch HCM
Staff B duoc approve loan duoi 50 trieu
Admin C duoc quan ly user nhung khong duoc xem giao dich
```

Ket luan de nho:

```text
RBAC = phan quyen theo vai tro.
ACL/Policy = phan quyen chi tiet theo user/group/role + action + resource.
Linux/AWS dung nhieu mo hinh gan permission chi tiet, dac biet la ACL/policy.
```

## 6. Checklist bao mat khi lam module Auth

- Password duoc hash bang BCrypt.
- Login sai khong noi ro email ton tai hay khong.
- JWT co expiration.
- JWT secret khong hard-code trong Java class.
- API private yeu cau Bearer token.
- API quan tri check role.
- Khong tra `password` trong response.
- Khong log full token/password.
- Register validate email/password/fullName.
- Refresh token co han su dung va co co che revoke/logout.
