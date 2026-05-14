package com.laptopshop.mapper;

import com.laptopshop.dto.VoucherDTO;
import com.laptopshop.entity.Voucher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VoucherMapper {
    @Mapping(target = "targetUserId", source = "targetUser.id")
    VoucherDTO toDto(Voucher voucher);

    @Mapping(target = "targetUser", ignore = true)
    Voucher toEntity(VoucherDTO dto);
}
