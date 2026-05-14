package com.laptopshop.controller.manager;

import com.laptopshop.security.SecurityUtils;
import com.laptopshop.service.InventoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/manager/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
@Tag(name = "Statistics & Inventory", description = "Báo cáo doanh thu và quản lý kho")
public class ManagerInventoryController {

    private final InventoryService inventoryService;

    @PutMapping("/{variantId}")
    public void updateInventory(@PathVariable Long variantId, @RequestParam Integer quantity) {
        Long branchId = SecurityUtils.getCurrentBranchId();
        inventoryService.updateStock(branchId, variantId, quantity);
    }
}
