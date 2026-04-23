package com.laptopshop.controller.manager;

import com.laptopshop.dto.PageResponseDTO;
import com.laptopshop.dto.VoucherDTO;
import com.laptopshop.service.VoucherService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/manager/vouchers")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
@Tag(name = "Voucher", description = "Quản lý mã giảm giá (Vouchers)")
public class ManagerVoucherController {

    private final VoucherService voucherService;

    @GetMapping
    public PageResponseDTO<VoucherDTO> getAllVouchers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return voucherService.getAllVouchers(page, size);
    }

    @GetMapping("/{id}")
    public VoucherDTO getVoucherById(@PathVariable Long id) {
        return voucherService.getVoucherById(id);
    }

    @PostMapping
    public VoucherDTO createVoucher(@RequestBody VoucherDTO dto) {
        return voucherService.createVoucher(dto);
    }

    @PutMapping("/{id}")
    public VoucherDTO updateVoucher(@PathVariable Long id, @RequestBody VoucherDTO dto) {
        return voucherService.updateVoucher(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteVoucher(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
    }
}
