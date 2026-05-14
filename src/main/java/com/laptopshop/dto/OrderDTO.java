package com.laptopshop.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private Long userId;
    private Long branchId;
    private String status;
    private Double totalPrice;
    private Double discountAmount;
    private String voucherCode;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;
}
