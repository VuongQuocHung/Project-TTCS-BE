package com.laptopshop.dto;

import com.laptopshop.entity.DiscountType;
import com.laptopshop.entity.VoucherStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherDTO {
    private Long id;
    private String code;
    private DiscountType discountType;
    private Double discountValue;
    private Double minOrderValue;
    private Double maxDiscountValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private Integer usedCount;
    private VoucherStatus status;
    private Long targetUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
