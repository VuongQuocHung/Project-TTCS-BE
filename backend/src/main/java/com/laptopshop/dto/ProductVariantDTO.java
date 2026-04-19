package com.laptopshop.dto;

import lombok.*;
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
    private Integer quantity; // Stock for a specific branch
}
