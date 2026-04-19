package com.laptopshop.controller.me;

import com.laptopshop.dto.OrderRequest;
import com.laptopshop.entity.Order;
import com.laptopshop.security.SecurityUtils;
import com.laptopshop.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/me/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "Quản lý đơn hàng (Orders)")
public class MeOrderController {

    private final OrderService orderService;

    @PostMapping
    public Order placeOrder(@Valid @RequestBody OrderRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return orderService.createOrder(request, userId);
    }

    @GetMapping
    public List<Order> getMyOrders() {
        Long userId = SecurityUtils.getCurrentUserId();
        return orderService.getMyOrders(userId);
    }

    @GetMapping("/{id}")
    public Order getMyOrderDetail(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return orderService.getMyOrderDetail(id, userId);
    }
}
