package com.laptopshop.controller.admin;

import com.laptopshop.dto.DashboardStatsDTO;
import com.laptopshop.dto.PageResponseDTO;
import com.laptopshop.dto.OrderDTO;
import com.laptopshop.entity.OrderStatus;
import com.laptopshop.service.DashboardService;
import com.laptopshop.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Order", description = "Quản lý đơn hàng (Orders)")
public class AdminOrderController {

    private final OrderService orderService;
    private final DashboardService dashboardService;

    @GetMapping
    public PageResponseDTO<OrderDTO> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.getAllOrders(status, branchId, userId, page, size);
    }

    @PutMapping("/{id}/status")
    public void updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        orderService.updateOrderStatus(id, status);
    }

    @GetMapping("/dashboard/stats")
    public DashboardStatsDTO getGlobalStats() {
        return dashboardService.getStats(null);
    }
}
