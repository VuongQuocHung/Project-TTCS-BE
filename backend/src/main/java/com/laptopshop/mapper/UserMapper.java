package com.laptopshop.mapper;

import com.laptopshop.dto.UserDTO;
import com.laptopshop.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "branchId", source = "branch.id")
    UserDTO toDto(User user);
}
