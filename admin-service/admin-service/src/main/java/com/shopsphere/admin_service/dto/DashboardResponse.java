package com.shopsphere.admin_service.dto;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class DashboardResponse {
    private long totalOrders;
    private long totalProducts;
    private double totalRevenue;
    private long pendingOrders;
    private long deliveredOrders;
}