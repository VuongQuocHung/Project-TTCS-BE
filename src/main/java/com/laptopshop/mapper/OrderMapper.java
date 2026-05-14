package com.laptopshop.mapper;

import com.laptopshop.dto.OrderDTO;
import com.laptopshop.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "branchId", source = "branch.id")
    @Mapping(target = "voucherCode", source = "voucher.code")
    OrderDTO toDto(Order order);
}
