package com.laptopshop.mapper;

import com.laptopshop.dto.BrandDTO;
import com.laptopshop.entity.Brand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BrandMapper {
    BrandDTO toDto(Brand brand);
    Brand toEntity(BrandDTO dto);
}
