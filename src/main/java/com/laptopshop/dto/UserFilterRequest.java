package com.laptopshop.dto;

import com.laptopshop.entity.Role;
import lombok.Data;

@Data
public class UserFilterRequest {
    private Role role;
    private Boolean enabled;
    private Long branchId;
    private String keyword;
}
