package com.laptopshop.controller.me;

import com.laptopshop.dto.OrderDTO;
import com.laptopshop.dto.OrderRequest;
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
    public OrderDTO placeOrder(@Valid @RequestBody OrderRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        return orderService.createOrder(request, userId);
    }

    @GetMapping
    public com.laptopshop.dto.PageResponseDTO<OrderDTO> getMyOrders(
            @org.springdoc.core.annotations.ParameterObject com.laptopshop.dto.OrderFilterRequest filterRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (filterRequest == null) filterRequest = new com.laptopshop.dto.OrderFilterRequest();
        filterRequest.setUserId(userId);
        return orderService.getAllOrders(filterRequest, page, size);
    }

    @GetMapping("/{id}")
    public OrderDTO getMyOrderDetail(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        return orderService.getMyOrderDetail(id, userId);
    }

    @PutMapping("/{id}/cancel")
    public void cancelOrder(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        orderService.cancelOrder(userId, id);
    }
}
