# 🏦 TPLite - Core Banking System

Dự án mô phỏng hệ thống Ngân hàng Lõi (Core Banking) tập trung vào tính toàn vẹn dữ liệu (ACID), xử lý đồng thời (Concurrency) và bảo mật.

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