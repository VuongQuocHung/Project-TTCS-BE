package com.laptopshop.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LowStockDTO {
    private Long branchId;
    private String branchName;
    private Long variantId;
    private String sku;
    private String productName;
    private Integer quantity;
}
