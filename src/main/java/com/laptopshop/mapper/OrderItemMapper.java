package com.laptopshop.mapper;

import com.laptopshop.dto.OrderItemDTO;
import com.laptopshop.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "variantId", source = "variant.id")
    @Mapping(target = "productName", source = "variant.product.name")
    @Mapping(target = "sku", source = "variant.sku")
    OrderItemDTO toDto(OrderItem orderItem);
}
