package com.laptopshop.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private String categoryName;
    private String brandName;
    private List<ProductVariantDTO> variants;
}
