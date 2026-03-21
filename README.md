# Project TTCS Backend (Laptop Store API)

Đây là mã nguồn Backend cho dự án bán Laptop (Laptop Store), được xây dựng bằng **Java Spring Boot**, **Hibernate (JPA)** và **Spring Security**. Cơ sở dữ liệu sử dụng là **Microsoft SQL Server**.

## Yêu cầu hệ thống (Prerequisites)
Để chạy dự án này trên máy cá nhân, bạn cần cài đặt:
- **Java Development Kit (JDK) 17** trở lên.
- **Microsoft SQL Server** (Bản Developer).

## Hướng dẫn cài đặt Cơ sở dữ liệu (Database Setup)
1. Mở **SQL Server Configuration Manager**, đảm bảo đã bật (Enable) giao thức TCP/IP ở port `1433` cho IP `127.0.0.1` (localhost).
2. Mở **SQL Server Management Studio (SSMS)**.
3. Tạo một database trống có tên: **`laptop_store`**.
```sql
CREATE DATABASE laptop_store;
```
*(Hệ thống Hibernate cấu hình `ddl-auto=update` sẽ tự động tạo các bảng (tables) khi chạy ứng dụng).*

## Cấu hình ứng dụng
Các cấu hình chính (Cổng server, Thông tin cấu hình Database, Tài khoản Security mặc định) nằm trong file `backend/src/main/resources/application.properties`:
- **Port:** `8080`
- **Database URL:** `jdbc:sqlserver://localhost:1433;databaseName=laptop_store...`
- **Database User/Pass:** `sa` / `123456`
- **Spring Security Mặc định:** `admin` / `123456`

*(Đảm bảo tài khoản `sa` trong SQL Server của bạn có mật khẩu tương ứng và đã được phép đăng nhập).*

## Hướng dẫn chạy dự án (Local Development)

### Cách 1: Chạy bằng Terminal/Command Prompt
1. Mở Terminal và di chuyển vào thư mục `backend`:
   ```bash
   cd backend
   ```
2. Thực thi lệnh sau để khởi động Spring Boot:
   ```bash
   # Dành cho Windows:
   .\mvnw.cmd spring-boot:run
   
   # Dành cho MacOS/Linux:
   ./mvnw spring-boot:run
   ```

## Kiểm tra / Test API
- Mở trình duyệt và truy cập: `http://localhost:8080`
- Nếu màn hình hiển thị form "Please sign in" của Spring Security, hãy đăng nhập với tài khoản:
  - **Username:** `admin`
  - **Password:** `123456`
- Bạn có thể sử dụng **Postman** hoặc **Insomnia** để kiểm thử các REST API (Nhớ truyền Basic Auth kèm Username/Password trên vào các request nếu chưa code token JWT).

## Cấu trúc thư mục cơ bản
```
backend/
 ├── src/main/java/com/ttcs/backend
 │    ├── controller/      # Nơi chứa các API Endpoints (Ví dụ: UserController, OrderController)
 │    ├── model/           # Nơi chứa các entities maps với bảng trong cơ sở dữ liệu
 │    ├── repository/      # Chứa các interface thao tác với DB qua Spring Data JPA
 │    └── service/         # Nơi chứa logic nghiệp vụ (Business logic)
 └── src/main/resources/
      └── application.properties # File cấu hình server và database
```