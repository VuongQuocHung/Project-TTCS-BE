package com.laptopshop.dto;

import com.laptopshop.entity.Role;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private Role role;
    private Long branchId;
    private boolean enabled;
}
