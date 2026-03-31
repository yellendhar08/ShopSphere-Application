package com.shopsphere.admin_service.service;

import com.shopsphere.admin_service.dto.ApiResponse;
import com.shopsphere.admin_service.dto.DashboardResponse;
import com.shopsphere.admin_service.feign.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderClient orderClient;
    private final CatalogClient catalogClient;

    @SuppressWarnings("unchecked")
    public DashboardResponse getDashboard() {

        ApiResponse<Object> ordersResp = orderClient.getAllOrders();
        ApiResponse<Object> productsResp = catalogClient.getAllProducts();

        List<Map<String, Object>> orders = (List<Map<String, Object>>) ordersResp.getData();
        Map<String, Object> productsData = (Map<String, Object>) productsResp.getData();

        long totalOrders = 0;
        long totalProducts = 0;
        double revenue = 0;
        long pending = 0;
        long delivered = 0;

        if (orders != null) {

            totalOrders = orders.size();

            revenue = orders.stream()
                    .filter(o -> "DELIVERED".equals(o.get("status")))
                    .mapToDouble(o -> ((Number) o.getOrDefault("totalAmount", 0)).doubleValue())
                    .sum();

            pending = orders.stream()
                    .filter(o -> "PAID".equals(o.get("status")) || "PACKED".equals(o.get("status")))
                    .count();

            delivered = orders.stream()
                    .filter(o -> "DELIVERED".equals(o.get("status")))
                    .count();
        }
        if (productsData != null) {
            totalProducts = ((Number) productsData.getOrDefault("totalElements", 0)).longValue();
        }

        return DashboardResponse.builder()
                .totalOrders(totalOrders)
                .totalProducts(totalProducts)
                .totalRevenue(revenue)
                .pendingOrders(pending)
                .deliveredOrders(delivered)
                .build();
    }
}