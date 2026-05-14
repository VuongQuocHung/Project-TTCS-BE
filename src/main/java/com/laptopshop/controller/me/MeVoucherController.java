package com.laptopshop.controller.me;

import com.laptopshop.dto.VoucherDTO;
import com.laptopshop.security.SecurityUtils;
import com.laptopshop.service.VoucherService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me/vouchers")
@RequiredArgsConstructor
@Tag(name = "Voucher", description = "Quản lý mã giảm giá (Vouchers)")
public class MeVoucherController {

    private final VoucherService voucherService;

    @GetMapping
    public List<VoucherDTO> getMyVouchers() {
        Long userId = SecurityUtils.getCurrentUserId();
        return voucherService.getMyVouchers(userId);
    }
}
