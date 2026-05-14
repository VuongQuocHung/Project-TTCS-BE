package com.laptopshop.controller.me;

import com.laptopshop.dto.ChangePasswordRequest;
import com.laptopshop.dto.UpdateProfileRequest;
import com.laptopshop.dto.UserDTO;
import com.laptopshop.security.SecurityUtils;
import com.laptopshop.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me/profile")
@RequiredArgsConstructor
@Tag(name = "User & Profile", description = "Quản lý người dùng và hồ sơ cá nhân")
public class MeProfileController {

    private final UserService userService;

    @GetMapping
    public UserDTO getProfile() {
        Long userId = SecurityUtils.getCurrentUserId();
        return userService.getProfile(userId);
    }

    @PutMapping
    public UserDTO updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return userService.updateProfile(userId, request);
    }

    @PutMapping("/password")
    public String changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.changePassword(userId, request);
        return "Password changed successfully";
    }
}
