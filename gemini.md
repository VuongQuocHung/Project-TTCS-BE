# LÕI KIẾN TRÚC HỆ THỐNG: MULTI-BRANCH LAPTOP SHOP BACKEND

## 1. TỔNG QUAN DỰ ÁN
- **Mục tiêu:** Xây dựng Backend cho hệ thống bán lẻ Laptop đa cơ sở.
- **Tech Stack:** Java 17+, Spring Boot 3.x, Spring Data JPA, Spring Security 6 (JWT), MySQL.
- **Thư viện:** Lombok, MapStruct (DTO mapping), Hibernate Validator, SpringDoc OpenAPI (Swagger).

## 2. QUY CHUẨN CODE (CODING STANDARDS)
- Kiến trúc phân lớp chuẩn: `Controller` -> `Service` -> `Repository` -> `Entity`.
- **Tuyệt đối không trả Entity trực tiếp ra Controller**. Mọi response/request phải qua DTO.
- Sử dụng `@Transactional` cho các hàm thay đổi dữ liệu (đặc biệt là Order và Inventory).
- Bắt buộc có Global Exception Handler (nhận `@RestControllerAdvice`) trả về format JSON lỗi chuẩn (timestamp, status, message, path).
- **Swagger UI:** Cấu hình đường dẫn `/docs` (springdoc.swagger-ui.path=/docs) và tích hợp nút nhập Bearer Token.

## 3. CẤU TRÚC DATABASE SẢN PHẨM & KHO (CORE DOMAIN)
Triển khai đúng các Entities sau kèm quan hệ JPA:
- `Category`, `Brand`: Quản lý danh mục và thương hiệu.
- `Product`: Thông tin chung (Tên, mô tả, liên kết Category & Brand).
- `ProductVariant`: SKU cấu hình cụ thể (Giá, màu sắc). 
  - **Lưu ý:** Chứa cột `specsJson` sử dụng `@JdbcTypeCode(SqlTypes.JSON)` để lưu Map thông số kỹ thuật.
- `ProductImage`: Hình ảnh gắn với Variant.
- `Branch`: Thông tin chi nhánh cửa hàng vật lý.
- `Inventory`: Bảng trung gian tồn kho. 
  - **Lưu ý:** Sử dụng `@EmbeddedId` ghép từ (`branch_id`, `variant_id`), chứa cột `quantity`.

## 4. QUY TRÌNH ETL (IMPORT DỮ LIỆU CÀO)
- **QUAN TRỌNG:** TUYỆT ĐỐI KHÔNG tạo JPA Entity cho dữ liệu raw.
- Xây dựng lớp `DataImportService` đọc file từ đường dẫn ngoài project (đọc giá trị `app.import.path` từ `application.properties`, ví dụ: `D:/laptop_data/raw_laptops.json`).
- Sử dụng `ObjectMapper` để parse file JSON này, bóc tách giá tiền, parse cấu hình vào cột `specsJson` và dùng JPA Repositories lưu thẳng vào các bảng `Product`, `ProductVariant` chính thức.

## 5. PHÂN QUYỀN BẢO MẬT & JWT (RBAC MATRIX)
Triển khai Spring Security 6 với cấu trúc 4 Roles và quy tắc chặn nghiêm ngặt:

### A. Nhóm API Public (Guest)
- Mở tự do (`permitAll()`): `/api/v1/auth/**` (Login/Register), `/docs/**`, `/v3/api-docs/**`.
- Mở tự do: `/api/v1/public/**` (Catalog, xem sản phẩm, xem tồn kho các chi nhánh).

### B. Nhóm Khách Hàng (CUSTOMER)
- Định tuyến: `/api/v1/me/**`.
- Yêu cầu xác thực JWT. Controller KHÔNG nhận `userId` từ body/URL.
- **Bắt buộc:** Dùng `SecurityContextHolder` lấy ID người dùng hiện tại. 
- Mọi hàm Repository truy xuất đơn hàng phải dùng `Optional<Order> findByIdAndUserId(Long id, Long userId)` để chống IDOR. Đặt hàng tự gán cho User đang đăng nhập.

### C. Nhóm Quản Lý Chi Nhánh (MANAGER)
- Định tuyến: `/api/v1/manager/**`.
- Có thuộc tính `branch_id` trong Entity User.
- **Quy tắc Method Security (`@PreAuthorize`):**
  - CHỈ được cập nhật kho (Inventory) NẾU branchId truyền lên khớp với branchId của Token.
  - CHỈ được xử lý đơn hàng (Order) NẾU đơn hàng đó thuộc branchId của Manager này.
  - TUYỆT ĐỐI KHÔNG được sửa giá, xóa sản phẩm, thêm hãng.

### D. Nhóm Quản Trị Hệ Thống (ADMIN)
- Định tuyến: `/api/v1/admin/**`.
- Yêu cầu `hasRole('ADMIN')`.
- Full quyền CRUD hệ thống. Kích hoạt API gọi hàm `DataImportService` để cào dữ liệu. Có quyền ghi đè kho mọi chi nhánh.

## LỜI NHẮC TRƯỚC KHI SINH CODE:
Hãy phân tích kỹ các cấu trúc trên, đảm bảo các Entity được tạo có đủ annotation `@ManyToOne`, `@OneToMany` với `CascadeType` hợp lý. Viết code sạch, tối ưu và comment bằng tiếng Việt cho các logic phức tạp như check Inventory khi đặt hàng.