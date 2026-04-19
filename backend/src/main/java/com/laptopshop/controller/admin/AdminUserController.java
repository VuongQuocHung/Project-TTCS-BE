package com.laptopshop.controller.admin;

import com.laptopshop.dto.UserDTO;
import com.laptopshop.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "User & Profile", description = "Quản lý người dùng và hồ sơ cá nhân")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/{userId}/assign-branch/{branchId}")
    public UserDTO assignBranch(@PathVariable Long userId, @PathVariable Long branchId) {
        return userService.assignManagerToBranch(userId, branchId);
    }

    @PutMapping("/{userId}/status")
    public void toggleUserStatus(@PathVariable Long userId, @RequestParam boolean enabled) {
        userService.toggleUserStatus(userId, enabled);
    }
}
