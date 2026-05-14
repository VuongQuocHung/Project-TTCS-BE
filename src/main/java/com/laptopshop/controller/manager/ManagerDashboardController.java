package com.laptopshop.controller.manager;

import com.laptopshop.dto.DashboardStatsDTO;
import com.laptopshop.dto.LowStockDTO;
import com.laptopshop.security.SecurityUtils;
import com.laptopshop.service.DashboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manager/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
@Tag(name = "Statistics & Inventory", description = "Báo cáo doanh thu và quản lý kho")
public class ManagerDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public DashboardStatsDTO getBranchStats() {
        Long branchId = SecurityUtils.getCurrentBranchId();
        return dashboardService.getStats(branchId);
    }

    @GetMapping("/low-stock")
    public List<LowStockDTO> getBranchLowStock() {
        Long branchId = SecurityUtils.getCurrentBranchId();
        return dashboardService.getLowStockAlerts(branchId);
    }
}
