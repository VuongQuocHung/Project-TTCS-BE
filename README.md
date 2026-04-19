# 💻 Multi-branch Laptop Shop Backend (Spring Boot 3)

Hệ thống Backend mạnh mẽ, bảo mật và có khả năng mở rộng cho chuỗi cửa hàng Laptop đa chi nhánh. Xây dựng theo tiêu chuẩn chuẩn chỉnh với kiến trúc phân lớp, bảo mật JWT và cơ chế quản lý kho đa điểm.

---

## 🚀 Tính năng nổi bật

- **Quản lý Đa Chi nhánh**: Quản lý tồn kho (`Inventory`) và đơn hàng (`Order`) tách biệt theo từng chi nhánh (`Branch`).
- **Phân quyền chặt chẽ (RBAC)**: 
  - `GUEST`: Xem sản phẩm, tồn kho, đăng ký.
  - `CUSTOMER`: Quản lý profile, đặt hàng (Bảo mật IDOR).
  - `MANAGER`: Quản lý kho và đơn hàng của riêng chi nhánh mình.
  - `ADMIN`: Quản trị toàn hệ thống, cấu hình giá, chạy tool ETL.
- **ETL Data Import**: Hệ thống hỗ trợ import dữ liệu từ file JSON bên ngoài (`raw_laptops.json`) để đồng bộ hóa danh mục, sản phẩm và biến thể.
- **Bảo mật JWT**: Xác thực Stateless dựa trên Token, tích hợp sẵn vào Swagger UI.
- **Tài liệu API tự động**: Swagger UI được cấu hình tại đường dẫn `/docs`.

---

## 🛠️ Công nghệ sử dụng

- **Core**: Java 17+, Spring Boot 3.2.4
- **Database**: MySQL 8.0, Spring Data JPA, Hibernate
- **Security**: Spring Security 6, io.jsonwebtoken (JJWT)
- **Mapping**: MapStruct (Entity <-> DTO)
- **Validation**: Hibernate Validator
- **Docs**: SpringDoc OpenAPI (Swagger UI)
- **Utilities**: Lombok, Jackson (JSON processing)

---

## 📋 Yêu cầu & Cài đặt

### 1. Yêu cầu hệ thống
- **JDK 17** hoặc cao hơn.
- **Maven 3.8+**.
- **MySQL 8.0+**.

### 2. Cấu hình Cơ sở dữ liệu
Hệ thống sẽ tự động tạo database nếu chưa có thông qua cấu hình `createDatabaseIfNotExist=true`.
Tuy nhiên, bạn nên đảm bảo MySQL đang chạy với thông tin:
- **User**: `root`
- **Password**: `123456`
- **Database Name**: `laptop_shop`

### 3. Cấu hình ứng dụng
Mọi cấu hình nằm tại `backend/src/main/resources/application.properties`.
Lưu ý đường dẫn file Import:
- `app.import.path=/home/ducva/Project-TTCS-BE/laptop_data/raw_laptops.json`

---

## 🏃 Hướng dẫn khởi chạy

Để khởi chạy dự án, bạn cần thực hiện lệnh Maven từ thư mục `backend`:

```bash
# Di chuyển vào thư mục backend
cd backend

# Chạy ứng dụng
mvn spring-boot:run
```

---

## 📖 Tài liệu API (Swagger UI)

Hệ thống cung cấp giao diện Swagger UI vô cùng tiện lợi tại:
📍 **[http://localhost:8080/docs](http://localhost:8080/docs)**

### Hướng dẫn Test API bảo mật:
1. Sử dụng API `/api/v1/auth/login` để lấy `token`.
2. Trên giao diện Swagger, nhấn nút **Authorize** (biểu tượng ổ khóa).
3. Nhập token theo định dạng: `Bearer <your_token_here>`.
4. Bây giờ bạn có thể gọi các API thuộc nhóm `/api/v1/me/**`, `/api/v1/manager/**` hoặc `/api/v1/admin/**`.

---

## 📂 Câu trúc thư mục (Backend)

`backend/src/main/java/com/laptopshop/`
- `config/`: Cấu hình Security, Swagger, Bean Mapping.
- `controller/`: Các RestController chia theo nhóm Auth, Public, Me, Manager, Admin.
- `dto/`: Request/Response DTOs.
- `entity/`: JPA Entities (Category, Brand, Product, Order, User, v.v.).
- `exception/`: Xử lý ngoại lệ toàn cục (Global Exception Handler).
- `mapper/`: MapStruct interfaces.
- `repository/`: Spring Data JPA Repositories.
- `security/`: JWT logic, Auth Filter, UserPrincipal.
- `service/`: Core Business Logic (Order, Inventory, ETL Service).

---

## 🧪 Dữ liệu mẫu (Laptop Data)
Dữ liệu mẫu (`raw_laptops.json`) được đặt tại thư mục `laptop_data/` ở thư mục gốc của dự án. File này được thiết kế để khớp hoàn toàn với logic ETL của backend.