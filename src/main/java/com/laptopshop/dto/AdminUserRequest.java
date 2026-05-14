package com.laptopshop.dto;

import com.laptopshop.entity.Role;
import lombok.Data;

@Data
public class AdminUserRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Role role;
    private Long branchId;
    private Boolean enabled;
}
