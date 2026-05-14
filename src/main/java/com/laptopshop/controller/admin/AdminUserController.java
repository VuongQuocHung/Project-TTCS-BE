package com.laptopshop.controller.admin;

import com.laptopshop.dto.AdminUserRequest;
import com.laptopshop.dto.UserDTO;
import com.laptopshop.service.UserService;
import com.laptopshop.entity.Role;
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
    public com.laptopshop.dto.PageResponseDTO<UserDTO> getAllUsers(
            @org.springdoc.core.annotations.ParameterObject com.laptopshop.dto.UserFilterRequest filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (filterRequest == null) filterRequest = new com.laptopshop.dto.UserFilterRequest();
        return userService.getUsers(filterRequest, page, size);
    }

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping
    public UserDTO createUser(@RequestBody AdminUserRequest request) {
        return userService.createUser(request);
    }

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody AdminUserRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/{userId}/assign-branch/{branchId}")
    public UserDTO assignBranch(@PathVariable Long userId, @PathVariable Long branchId) {
        return userService.assignManagerToBranch(userId, branchId);
    }

    @PutMapping("/{userId}/status")
    public void toggleUserStatus(@PathVariable Long userId, @RequestParam boolean enabled) {
        userService.toggleUserStatus(userId, enabled);
    }

    @PutMapping("/{userId}/role")
    public void updateUserRole(@PathVariable Long userId, @RequestParam Role role) {
        userService.updateUserRole(userId, role);
    }
}
