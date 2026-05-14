package com.laptopshop.mapper;

import com.laptopshop.dto.CategoryDTO;
import com.laptopshop.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDto(Category category);
    Category toEntity(CategoryDTO dto);
}
