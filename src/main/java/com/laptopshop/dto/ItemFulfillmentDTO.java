package com.laptopshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemFulfillmentDTO {
    private Long variantId;
    private Integer requestedQuantity;
    private Integer availableQuantity;
    private Boolean isAvailable;
}
