package com.ttcs.backend.controller;

import com.ttcs.backend.auth.dto.AuthResponse;
import com.ttcs.backend.auth.dto.LoginRequest;
import com.ttcs.backend.auth.dto.RegisterRequest;
import com.ttcs.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "Quản lý đăng nhập & đăng ký")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản", description = "Tạo một tài khoản người dùng mới cho Laptop Shop")
    @ApiResponse(responseCode = "201", description = "Đăng ký thành công")
    @ApiResponse(responseCode = "400", description = "Lỗi dữ liệu đầu vào hoặc email đã tồn tại")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập hệ thống", description = "Xác thực người dùng và nhận JWT token")
    @ApiResponse(responseCode = "200", description = "Đăng nhập thành công")
    @ApiResponse(responseCode = "401", description = "Sai email hoặc mật khẩu")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
