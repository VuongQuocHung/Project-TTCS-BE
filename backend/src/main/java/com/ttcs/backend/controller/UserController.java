package com.ttcs.backend.controller;

import com.ttcs.backend.entity.User;
import com.ttcs.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "Quản lý người dùng (User)")
public class UserController {
    private final UserService userService;

    // 1. GET ALL
    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả người dùng", description = "Trả về danh sách toàn bộ người dùng trong hệ thống")
    @ApiResponse(responseCode = "200", description = "Thành công")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // 2. POST CREATE
    @PostMapping
    @Operation(summary = "Tạo mới người dùng", description = "Đăng ký hoặc tạo một tài khoản người dùng mới")
    @ApiResponse(responseCode = "200", description = "Thành công")
    @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        // Normally password should be encrypted here
        return ResponseEntity.ok(userService.createUser(user));
    }

    // 3. GET BY ID
    @GetMapping("/{id}")
    @Operation(summary = "Lấy thông tin người dùng theo ID", description = "Xem chi tiết thông tin của người dùng thông qua ID")
    @ApiResponse(responseCode = "200", description = "Thành công")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // 4. PUT/PATCH UPDATE
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật người dùng", description = "Cập nhật thông tin của người dùng hiện tại dựa trên ID")
    @ApiResponse(responseCode = "200", description = "Thành công")
    @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    // 5. DELETE
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa người dùng", description = "Xóa một tài khoản người dùng khỏi hệ thống thông qua ID")
    @ApiResponse(responseCode = "204", description = "Xóa thành công")
    @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
