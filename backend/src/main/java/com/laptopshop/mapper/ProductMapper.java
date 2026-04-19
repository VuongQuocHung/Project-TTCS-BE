package com.laptopshop.mapper;

import com.laptopshop.dto.ProductDTO;
import com.laptopshop.dto.ProductVariantDTO;
import com.laptopshop.entity.Product;
import com.laptopshop.entity.ProductVariant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "brandName", source = "brand.name")
    ProductDTO toDto(Product product);

    @Mapping(target = "quantity", ignore = true) // Set manually based on branch
    ProductVariantDTO toDto(ProductVariant variant);
}
