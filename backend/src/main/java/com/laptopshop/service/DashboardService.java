package com.laptopshop.service;

import com.laptopshop.dto.DashboardStatsDTO;
import com.laptopshop.dto.LowStockDTO;
import com.laptopshop.entity.Inventory;
import com.laptopshop.entity.Order;
import com.laptopshop.entity.OrderStatus;
import com.laptopshop.repository.InventoryRepository;
import com.laptopshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;

    public DashboardStatsDTO getStats(Long branchId) {
        List<Order> orders;
        if (branchId != null) {
            orders = orderRepository.findByBranchId(branchId);
        } else {
            orders = orderRepository.findAll();
        }

        long totalOrders = orders.size();
        double totalRevenue = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .mapToDouble(Order::getTotalPrice)
                .sum();
        
        long successfulOrders = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.DELIVERED)
                .count();

        Map<String, Double> revenueByStatus = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getStatus().name(), 
                        Collectors.summingDouble(Order::getTotalPrice)));

        return DashboardStatsDTO.builder()
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .successfulOrders(successfulOrders)
                .revenueByStatus(revenueByStatus)
                .build();
    }

    public List<LowStockDTO> getLowStockAlerts(Long branchId) {
        List<Inventory> lowStockItems;
        if (branchId != null) {
            lowStockItems = inventoryRepository.findByBranchIdAndQuantityLessThan(branchId, 5);
        } else {
            lowStockItems = inventoryRepository.findByQuantityLessThan(5);
        }

        return lowStockItems.stream()
                .map(item -> LowStockDTO.builder()
                        .branchId(item.getBranch().getId())
                        .branchName(item.getBranch().getName())
                        .variantId(item.getVariant().getId())
                        .sku(item.getVariant().getSku())
                        .productName(item.getVariant().getProduct().getName())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }
}
