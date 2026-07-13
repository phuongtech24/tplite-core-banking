# Junior Backend Infra Roadmap - TPBank Prep

File nay dung de on tap cac chu de backend infra thuong gap khi phong van Junior Java/Spring Boot:

- CI/CD voi Jenkins
- Cache voi Redis
- Message Queue voi Kafka
- Microservice co ban

Muc tieu:

- Hieu dung khai niem.
- Biet no giai quyet van de gi.
- Tra loi duoc cau hoi phong van muc junior.
- Lien he duoc voi project banking/Spring Boot.

## 1. CI/CD - Jenkins

### 1.1 CI/CD la gi?

CI/CD la quy trinh tu dong hoa viec build, test va deploy ung dung.

```text
CI = Continuous Integration
CD = Continuous Delivery / Continuous Deployment
```

Noi de hieu:

```text
Developer push code -> Jenkins tu build -> tu test -> neu pass thi deploy
```

### 1.2 Jenkins la gi?

Jenkins la cong cu automation server, hay dung de lam CI/CD pipeline.

Jenkins giup:

- Lay code tu Git.
- Build project.
- Chay test.
- Dong goi file `.jar` hoac Docker image.
- Deploy len server.
- Bao ket qua pass/fail.

### 1.3 Pipeline co ban

```text
Checkout code
Build
Test
Package
Deploy
```

Vi du voi Spring Boot:

```text
1. Jenkins pull code tu Git
2. Chay mvn clean test
3. Chay mvn package
4. Tao file core-banking.jar
5. Copy jar len server
6. Restart service
```

### 1.4 Jenkinsfile la gi?

`Jenkinsfile` la file mo ta pipeline bang code.

Vi du don gian:

```groovy
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
    }
}
```

### 1.5 Cau hoi phong van Jenkins

**Jenkins dung de lam gi?**

```text
Jenkins dung de tu dong hoa CI/CD. Khi developer push code, Jenkins co the tu pull code, build, test, package va deploy ung dung.
```

**CI khac CD the nao?**

```text
CI tap trung vao viec tich hop code thuong xuyen, build va test tu dong. CD la buoc tiep theo, tu dong dua ung dung len moi truong test/staging/production.
```

**Neu build fail thi sao?**

```text
Pipeline dung lai, Jenkins bao loi. Developer xem log, sua loi, push code lai.
```

**Junior can biet den muc nao?**

- Biet CI/CD la gi.
- Biet pipeline co nhung buoc nao.
- Biet Jenkins co the build/test/deploy Spring Boot.
- Doc hieu Jenkinsfile co ban.

## 2. Cache - Redis

### 2.1 Cache la gi?

Cache la bo nho tam dung de luu du lieu hay duoc truy cap, giup doc nhanh hon va giam tai database.

Noi de hieu:

```text
Du lieu nao hoi DB nhieu lan thi luu tam vao cache.
Lan sau doc cache truoc, khong can vao DB nua.
```

### 2.2 Redis la gi?

Redis la in-memory data store, du lieu nam trong RAM nen doc/ghi rat nhanh.

Redis thuong dung de:

- Cache data.
- Luu session.
- Rate limiting.
- Distributed lock.
- Queue nhe.
- Luu OTP/token ngan han.

### 2.3 Luong cache co ban

```text
Client goi API
Service check Redis
Neu co data -> tra ve ngay
Neu khong co -> query DB
Save data vao Redis
Tra ve client
```

### 2.4 Vi du trong banking

Co the cache:

- Thong tin cau hinh he thong.
- Danh sach chi nhanh.
- Danh muc san pham vay.
- Permission/role read-only.
- OTP ngan han.

Khong nen cache tuy tien:

- So du tai khoan neu khong co co che dong bo chat.
- Du lieu giao dich tien nhay cam.
- Du lieu thay doi lien tuc ma khong co invalidation.

### 2.5 TTL la gi?

TTL = Time To Live.

Nghia la thoi gian song cua data trong cache.

Vi du:

```text
OTP song 5 phut
Danh muc san pham song 1 gio
Access token blacklist song den khi token het han
```

### 2.6 Cache invalidation la gi?

Cache invalidation la xoa/cap nhat cache khi data goc thay doi.

Vi du:

```text
Admin update loan product
Phai xoa cache loan_products
Lan sau API query DB lai va cache data moi
```

### 2.7 Cau hoi phong van Redis

**Redis dung de lam gi?**

```text
Redis dung de cache du lieu, luu session/token ngan han, rate limiting hoac distributed lock. Vi du trong Spring Boot, Redis giup giam so lan query database cho nhung du lieu doc nhieu.
```

**Cache aside pattern la gi?**

```text
Service doc cache truoc. Neu cache miss thi query database, sau do ghi data vao cache va tra response.
```

**TTL la gi?**

```text
TTL la thoi gian ton tai cua key trong Redis. Het TTL thi key tu dong bi xoa.
```

**Van de lon nhat khi dung cache la gi?**

```text
Du lieu cache co the bi cu so voi database, nen can co chien luoc TTL va invalidation hop ly.
```

## 3. Message Queue - Kafka

### 3.1 Message Queue la gi?

Message queue giup cac service giao tiep bat dong bo voi nhau.

Noi de hieu:

```text
Service A khong goi truc tiep Service B.
Service A gui message vao queue/topic.
Service B doc message va xu ly sau.
```

### 3.2 Kafka la gi?

Kafka la distributed event streaming platform, thuong dung de xu ly message/event voi throughput cao.

Kafka co cac thanh phan chinh:

- Producer: ben gui message.
- Consumer: ben doc message.
- Topic: kenh chua message.
- Partition: chia topic de scale.
- Consumer group: nhom consumer cung xu ly message.
- Broker: server Kafka.

### 3.3 Luong Kafka co ban

```text
Producer -> Topic -> Consumer
```

Vi du banking:

```text
TransferService tao giao dich thanh cong
Publish event TRANSACTION_SUCCESS vao Kafka
NotificationService consume event
Gui thong bao cho user
```

### 3.4 Vi sao dung Kafka?

Dung Kafka khi:

- Muon xu ly bat dong bo.
- Muon tach service de giam phu thuoc truc tiep.
- Muon retry khi service nhan dang loi.
- Muon xu ly event log/giao dich so luong lon.

### 3.5 Kafka vs REST API

| REST API | Kafka |
| --- | --- |
| Dong bo | Bat dong bo |
| Service A cho Service B tra ve | Service A gui message roi di tiep |
| De hieu, de debug | Tot cho event, async, scale |
| Phu hop query/request-response | Phu hop event/notification/log |

### 3.6 Cau hoi phong van Kafka

**Kafka dung de lam gi?**

```text
Kafka dung de truyen message/event bat dong bo giua cac service. No giup decouple service, xu ly event voi throughput cao va ho tro consumer group de scale.
```

**Producer, consumer, topic la gi?**

```text
Producer la ben gui message, consumer la ben nhan message, topic la noi message duoc publish vao.
```

**Vi du dung Kafka trong banking?**

```text
Sau khi giao dich thanh cong, TransferService publish event. NotificationService consume event de gui thong bao, AuditService consume event de ghi log.
```

**Tai sao khong goi thang NotificationService bang REST?**

```text
Neu goi REST dong bo, giao dich co the bi cham hoac fail vi NotificationService loi. Dung Kafka thi giao dich chinh co the thanh cong truoc, notification xu ly bat dong bo sau.
```

## 4. Microservice Co Ban

### 4.1 Microservice la gi?

Microservice la kien truc chia he thong thanh nhieu service nho, moi service phu trach mot nghiep vu rieng.

Vi du:

```text
Auth Service
Customer Service
Account Service
Transfer Service
Loan Service
Notification Service
```

Moi service co the:

- Codebase rieng.
- Database rieng.
- Deploy rieng.
- Scale rieng.

### 4.2 Monolith vs Microservice

| Monolith | Microservice |
| --- | --- |
| Mot ung dung lon | Nhieu service nho |
| De hoc, de debug ban dau | De scale tung phan |
| Deploy ca he thong | Deploy tung service |
| It phuc tap infra | Can API Gateway, service discovery, monitoring |

### 4.3 Junior nen hieu microservice den muc nao?

Can hieu:

- Vi sao tach service.
- Service giao tiep qua REST/Kafka.
- Moi service nen co database rieng.
- Can API Gateway.
- Can config, logging, tracing, monitoring.
- Microservice phuc tap hon monolith.

Chua can master:

- Kubernetes nang cao.
- Service mesh.
- Distributed transaction phuc tap.
- Saga pattern nang cao.

### 4.4 Cac thanh phan hay gap

```text
API Gateway
Service Discovery
Config Server
Auth Service
Message Broker
Distributed Tracing
Centralized Logging
Monitoring
```

### 4.5 Vi du tach tu project core banking

Ban dau project minh dang la monolith:

```text
core-banking
  auth
  customer
  account
  transfer
  loan
  notification
```

Sau nay neu tach microservice:

```text
auth-service
customer-service
account-service
transfer-service
loan-service
notification-service
```

Flow vi du:

```text
Client -> API Gateway -> Transfer Service
Transfer Service -> Account Service de check balance
Transfer Service -> Kafka event TRANSACTION_SUCCESS
Notification Service -> consume event -> gui thong bao
Audit Service -> consume event -> ghi audit log
```

### 4.6 Cau hoi phong van Microservice

**Microservice la gi?**

```text
Microservice la kien truc chia ung dung thanh nhieu service nho, moi service phu trach mot domain/nghiep vu rieng va co the deploy/scale doc lap.
```

**Uu diem?**

```text
De scale tung service, deploy doc lap, team lam viec doc lap, phu hop he thong lon.
```

**Nhuoc diem?**

```text
Phuc tap hon monolith, kho debug hon, can monitoring/logging/tracing, can xu ly loi network va data consistency.
```

**Khi nao khong nen dung microservice?**

```text
Khi he thong con nho, team it nguoi, domain chua ro, chua co kinh nghiem DevOps/monitoring. Khi do monolith modular se tot hon.
```

## 5. Lo Trinh On Tap 7 Ngay

### Ngay 1 - Jenkins CI/CD

- Hieu CI/CD.
- Hieu Jenkins pipeline.
- Doc Jenkinsfile co ban.
- Tra loi duoc CI vs CD.

### Ngay 2 - Redis Cache

- Hieu cache aside.
- Hieu TTL.
- Hieu cache invalidation.
- Biet vi du Redis trong Spring Boot.

### Ngay 3 - Kafka

- Hieu producer, consumer, topic, partition.
- Hieu async vs sync.
- Biet use case notification/audit.

### Ngay 4 - Microservice

- Hieu monolith vs microservice.
- Hieu API Gateway, service discovery.
- Biet nhung kho khan cua microservice.

### Ngay 5 - Lien He Project Banking

- Auth co the tach auth-service.
- Transfer publish event.
- Notification consume event.
- Redis cache config/OTP.
- Jenkins build/deploy app.

### Ngay 6 - Luyen Cau Hoi Phong Van

Tap tra loi:

- Jenkins dung de lam gi?
- Redis cache giup gi?
- Kafka khac REST the nao?
- Microservice co uu/nhuoc diem gi?
- Tai sao project nho nen lam monolith modular truoc?

### Ngay 7 - Tong On

- Ve so do kien truc.
- Noi flow end-to-end:

```text
Client -> API -> DB
Client -> API -> Redis
Transfer -> Kafka -> Notification
Git push -> Jenkins -> Build/Test/Deploy
```

## 6. Cau Tra Loi Gop Khi Phong Van

```text
Trong project hoc tap cua em, em dang lam theo monolith modular truoc de nam nghiep vu. Em hieu khi he thong lon co the tach thanh microservices nhu auth-service, account-service, transfer-service va notification-service. Jenkins co the dung de tu dong build/test/deploy. Redis dung de cache du lieu doc nhieu hoac luu OTP/token ngan han. Kafka dung de xu ly bat dong bo, vi du sau khi transfer thanh cong thi publish event de notification-service gui thong bao va audit-service ghi log.
```

## 7. Checklist Junior

- Giai thich duoc CI/CD.
- Biet Jenkins pipeline gom nhung buoc nao.
- Giai thich duoc Redis cache va TTL.
- Biet cache invalidation la gi.
- Giai thich duoc Kafka producer/consumer/topic.
- Biet khi nao dung Kafka thay REST.
- Giai thich duoc monolith vs microservice.
- Biet microservice co them phuc tap ve deploy, logging, monitoring, network.
- Lien he duoc vao project banking cua minh.

## 8. Core vs Nice-To-Have Cho Junior TPBank

Bang nay dung de uu tien hoc. Khong hoc lan man.

| Nhom | Muc can hoc | Uu tien |
| --- | --- | --- |
| Java Core + OOP | Hieu sau, giai thich bang vi du thuc te | 1 |
| Spring Boot + JPA + Security | Lam duoc, giai thich duoc flow | 1 |
| Database: ACID, Index, Lock | Hieu sau, thuc hanh PostgreSQL | 1 |
| Redis | Biet 3 use case: OTP, Cache, Rate limit | 2 |
| Kafka | Biet Producer/Consumer, tai sao dung | 2 |
| Microservice | Biet concept + API Gateway | 2 |
| Design Pattern | Tap trung nhom GoF thuc chien: Singleton, Factory, Strategy, Proxy, Builder | 3 |

Ket luan:

```text
Java Core + Spring + Database la phan quyet dinh pass.
Redis + Kafka + MSA + Design Pattern hoc dung muc Junior, khong dao qua sau.
```

## 9. Kafka - Hoc Dung 3 Thu

### 9.1 Tai sao can Kafka?

Khong dung Kafka:

```text
Transfer thanh cong -> goi truc tiep EmailService
Email server chet -> Transfer cung fail
```

Day la thiet ke khong tot vi notification khong nen lam hong luong chuyen tien chinh.

Dung Kafka:

```text
Transfer commit DB thanh cong
-> publish event transfer-completed
-> return response ngay
-> Notification consume async
-> neu email server loi, message van con de retry
```

### 9.2 Producer/Consumer co ban

Producer day event:

```java
kafkaTemplate.send("transfer-completed", event);
```

Consumer nhan event:

```java
@KafkaListener(topics = "transfer-completed")
public void handle(TransferEvent event) {
    emailService.send(event);
}
```

### 9.3 Cau tra loi phong van chuan

```text
Em dung Kafka de tach Notification ra khoi luong chinh. Sau khi chuyen tien commit DB xong, em publish event vao Kafka va return ngay. Notification Service consume bat dong bo. Neu email server loi thi message van nam trong Kafka va co the retry, luong transfer chinh khong bi anh huong.
```

Khong can hoc sau cho Junior:

- Kafka cluster.
- Partition rebalance.
- Exactly-once semantic.
- Kafka Streams.

## 10. Redis - Hoc Dung 3 Use Case

### 10.1 OTP

Tai sao Redis hop OTP?

```text
Redis co TTL native, tu xoa OTP sau 5 phut, khong can cleanup job.
```

Vi du:

```java
redisTemplate.opsForValue()
    .set("otp:" + userId, otpCode, 5, TimeUnit.MINUTES);
```

### 10.2 Cache

Vi du cache danh muc hoac cau hinh doc nhieu.

```java
@Cacheable(value = "loanProducts", key = "#code")
public LoanProduct getLoanProduct(String code) {
    return loanProductRepository.findByCode(code)
            .orElseThrow();
}
```

Khi update thi xoa cache:

```java
@CacheEvict(value = "loanProducts", key = "#code")
public void updateLoanProduct(String code, UpdateLoanProductRequest request) {
    // update DB
}
```

Luu y banking:

```text
Cache so du tai khoan phai rat can than vi lien quan tien.
Neu hoc Junior, noi cache balance ngan han va phai evict sau transfer la du.
```

### 10.3 Rate Limiting

Dung Redis `INCR` vi atomic.

```java
Long count = redisTemplate.opsForValue().increment("ratelimit:" + userId);
if (count == 1) {
    redisTemplate.expire("ratelimit:" + userId, 1, TimeUnit.MINUTES);
}
if (count > 10) {
    throw new RateLimitException();
}
```

### 10.4 Cau tra loi phong van chuan

```text
Em dung Redis cho 3 viec: luu OTP voi TTL 5 phut tu xoa, cache du lieu doc nhieu de giam tai DB, va rate limiting API bang INCR atomic de chong spam.
```

Khong can hoc sau cho Junior:

- Redis Cluster.
- Redis Sentinel.
- Lua scripting.
- Redis Streams.

## 11. Microservice - Hoc Dung 4 Khai Niem

### 11.1 Tai sao tach service?

Phai co ly do cu the, khong tach chi vi nghe "xịn".

Vi du:

```text
Auth Service: scale rieng khi nhieu user login.
Banking Service: core nghiep vu, can ACID chat.
Notification Service: async, khong can real-time, tach hop ly nhat.
```

### 11.2 API Gateway lam gi?

```text
Client -> API Gateway -> Auth Service
                     -> Banking Service
                     -> Notification Service
```

Gateway co the lo:

- Routing.
- JWT validation.
- Rate limit.
- CORS.
- Logging request.

### 11.3 Khi nao khong nen dung Microservice?

```text
Khi he thong con nho, team it nguoi, domain chua ro, chua co monitoring/DevOps tot. Microservice lam tang phuc tap: network failure, debug kho, distributed transaction.
```

### 11.4 Distributed Transaction

Trong monolith:

```text
@Transactional co the bao het logic transfer trong cung DB.
```

Trong microservice:

```text
Transfer nam Banking DB.
Notification nam Notification DB.
Khong co mot @Transactional bao duoc ca hai DB/service.
```

Cach noi vua du Junior:

```text
Voi notification, em chap nhan eventual consistency. Banking commit truoc, sau do publish event Kafka de Notification xu ly sau.
```

Khong can hoc sau cho Junior:

- Service Mesh.
- Istio.
- Kubernetes nang cao.
- Saga pattern implement.
- Event Sourcing.

## 12. Design Pattern - Nhom GoF Thuc Chien

Voi Junior Spring Boot, khong can hoc tat ca design pattern. Tap trung vao nhom xuat hien nhieu nhat khi code backend:

```text
Singleton
Factory Method
Strategy
Proxy
Builder
```

Luu y:

```text
DTO, Service Layer, Repository la pattern/kien truc ung dung hay dung trong Spring Boot.
Singleton, Factory, Strategy, Proxy, Builder la nhom design pattern GoF/object creation/behavior thuc chien can nhan ra.
```

### 12.1 Strategy Pattern

Dung khi co nhieu cach xu ly cung mot nghiep vu va muon doi linh hoat.

Vi du banking/payment:

```text
PaymentStrategy
  - NapasPaymentStrategy
  - VisaPaymentStrategy
  - VnPayPaymentStrategy
  - InternalTransferStrategy
```

Khi nao dung:

- Co nhieu loai payment.
- Co nhieu cach tinh phi.
- Co nhieu cach tinh lai loan.
- Co nhieu kenh notification: EMAIL, SMS, IN_APP.

Cau tra loi phong van:

```text
Strategy Pattern giup dong goi tung cach xu ly vao mot class rieng. Vi du thanh toan qua Napas, Visa, VNPay co logic khac nhau, em co the tao PaymentStrategy interface va moi phuong thuc thanh toan la mot implementation. Khi them phuong thuc moi, em them class moi thay vi sua if/else lon.
```

### 12.2 Factory Method

Dung khi can khoi tao object theo type/lua chon cua user.

Vi du banking:

```text
AccountFactory.create(AccountType.SAVING)
AccountFactory.create(AccountType.CREDIT_CARD)
AccountFactory.create(AccountType.DEBIT_CARD)
```

Khi nao dung:

- Tao tai khoan theo loai: PAYMENT, SAVING, LOAN.
- Tao card theo loai: DEBIT, CREDIT, PREPAID.
- Tao notification theo channel: EMAIL, SMS, IN_APP.

Cau tra loi phong van:

```text
Factory Method giup gom logic tao object vao mot noi. Vi du user chon mo tai khoan tiet kiem hay the tin dung, em khong rai rac new object o nhieu service, ma dung factory de tao dung loai account/card.
```

### 12.3 Proxy Pattern

Dung khi muon chen logic truoc/sau method ma khong sua method goc.

Spring dung rat nhieu Proxy:

```text
@Transactional
@Cacheable
@Async
Spring AOP
```

Vi du:

```text
Service method goi transfer()
Spring tao proxy boc quanh method
Truoc method: mo transaction
Method chay
Neu success: commit
Neu exception: rollback
```

Cau tra loi phong van:

```text
@Transactional dung Proxy Pattern. Spring khong goi truc tiep object goc, ma goi qua proxy. Proxy mo transaction truoc khi method chay, neu method thanh cong thi commit, neu co runtime exception thi rollback.
```

### 12.4 Singleton Pattern

Spring Bean mac dinh la Singleton.

Nghia la:

```text
AuthService chi co 1 instance trong Spring container.
Nhieu request cung dung chung bean do.
```

Lien he Dependency Injection:

```text
Minh khong tu new AuthService.
Spring tao bean va inject vao controller/service can dung.
```

Cau tra loi phong van:

```text
Singleton Pattern dam bao mot class chi co mot instance dung chung. Trong Spring, bean mac dinh scope singleton, nen cac service/repository thuong chi co mot instance trong ApplicationContext va duoc inject vao noi can dung.
```

### 12.5 Builder Pattern

Dung khi tao object co nhieu field, tranh constructor qua dai va giup object creation ro rang hon.

Vi du:

```java
AuthResponse response = AuthResponse.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .accessToken(token)
        .tokenType("Bearer")
        .build();
```

Khi nao dung:

- Response object nhieu field.
- DTO/output can tao ro rang.
- Entity/domain object co nhieu optional field.

Cau tra loi phong van:

```text
Builder Pattern giup tao object co nhieu field mot cach de doc va an toan hon constructor dai. Trong Java/Spring Boot, em hay gap Builder khi tao DTO response, request test data, hoac object co nhieu optional field.
```

### 12.6 Cach hoc dung muc Junior

Can lam duoc:

- Nhan ra pattern trong code.
- Giai thich vi sao dung.
- Noi duoc trade-off.
- Lien he voi project banking.

Khong can:

- Hoc het 23 GoF patterns.
- Tu implement pattern phuc tap.
- Dung pattern cho moi bai toan.

Cau tra loi mau:

```text
Em tap trung vao mot so pattern thuc chien trong Spring Boot. Singleton xuat hien o Spring Bean mac dinh, Proxy nam trong @Transactional va @Cacheable, Strategy phu hop khi co nhieu cach thanh toan/tinh phi, Factory Method phu hop khi tao account/card theo type, Builder giup tao DTO/object nhieu field ro rang hon. Em khong ap dung pattern may moc, chi dung khi no lam code de mo rong va de doc hon.
```

## 13. Lo Trinh 8 Ngay Neu Sap Phong Van

### Ngay 1-3 - Hoan thien Banking project

- Transfer chay duoc voi Lock + ACID.
- Auth + JWT chay duoc.
- README + Swagger + Docker neu con thoi gian.

### Ngay 4-5 - Java Core + Spring Boot sau

- OOP 4 tinh chat + vi du thuc te.
- `@Transactional` va cac truong hop khong rollback.
- N+1 query va cach fix.

### Ngay 6 - Database sau

- ACID.
- 4 isolation level.
- Lost update.
- Index B-Tree.
- `EXPLAIN ANALYZE`.
- Optimistic vs Pessimistic Lock.

### Ngay 7 - Redis + Kafka + MSA

- Redis: OTP, Cache, Rate limit.
- Kafka: tai sao dung + Producer/Consumer.
- MSA: API Gateway + tai sao tach service.

### Ngay 8 - Mock interview

- Luyen 30 cau mix Java/Spring/DB/Security/Redis/Kafka.
- Tap pitch Banking project 3-5 phut.
