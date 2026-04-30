package com.laptopshop.controller.manager;

import com.laptopshop.dto.OrderDTO;
import com.laptopshop.dto.PageResponseDTO;
import com.laptopshop.entity.OrderStatus;
import com.laptopshop.security.SecurityUtils;
import com.laptopshop.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/manager/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
@Tag(name = "Order", description = "Quản lý đơn hàng (Orders)")
public class ManagerOrderController {

    private final OrderService orderService;

    @GetMapping
    public PageResponseDTO<OrderDTO> getBranchOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long branchId = SecurityUtils.getCurrentBranchId();
        return orderService.getAllOrders(status, branchId, null, page, size);
    }

    @PutMapping("/{id}/status")
    public void updateOrderStatus(@PathVariable Long id, @RequestParam OrderStatus status) {
        orderService.updateOrderStatus(id, status);
    }
}
