package com.laptopshop.controller.admin;

import com.laptopshop.dto.LowStockDTO;
import com.laptopshop.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/inventory")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Inventory", description = "Quản lý kho hàng (Inventory)")
public class AdminInventoryController {

    private final DashboardService dashboardService;

    @Operation(summary = "Get global low stock alerts")
    @GetMapping("/low-stock")
    public List<LowStockDTO> getGlobalLowStock() {
        return dashboardService.getLowStockAlerts(null);
    }
}
