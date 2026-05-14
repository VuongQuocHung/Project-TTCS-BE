package com.laptopshop.mapper;

import com.laptopshop.dto.BranchDTO;
import com.laptopshop.entity.Branch;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BranchMapper {
    BranchDTO toDto(Branch branch);
    Branch toEntity(BranchDTO dto);
}
