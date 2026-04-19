package com.laptopshop.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DashboardStatsDTO {
    private long totalOrders;
    private double totalRevenue;
    private long successfulOrders;
    private Map<String, Double> revenueByStatus;
}
