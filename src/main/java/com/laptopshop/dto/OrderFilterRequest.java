package com.laptopshop.dto;

import com.laptopshop.entity.OrderStatus;
import lombok.Data;

@Data
public class OrderFilterRequest {
    private OrderStatus status;
    private Long branchId;
    private Long userId;
}
