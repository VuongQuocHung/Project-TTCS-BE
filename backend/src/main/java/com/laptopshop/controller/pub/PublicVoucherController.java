package com.laptopshop.controller.pub;

import com.laptopshop.dto.VoucherDTO;
import com.laptopshop.service.VoucherService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/vouchers")
@RequiredArgsConstructor
@Tag(name = "Voucher", description = "Quản lý mã giảm giá (Vouchers)")
public class PublicVoucherController {

    private final VoucherService voucherService;

    @GetMapping("/validate")
    public VoucherDTO validateVoucher(
            @RequestParam String code,
            @RequestParam Double orderValue) {
        return voucherService.getVoucherByCode(code); // validation is naturally handled in Order placement, this is just for UI preview
    }
}
