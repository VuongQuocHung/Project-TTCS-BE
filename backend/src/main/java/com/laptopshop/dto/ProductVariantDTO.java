package com.laptopshop.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantDTO {
    private Long id;
    private String sku;
    private Double price;
    private String color;
    private Map<String, Object> specsJson;
    private Integer quantity; // Total or branch-specific quantity
    private List<InventoryDTO> inventories;
    private List<ProductImageDTO> images;
}
