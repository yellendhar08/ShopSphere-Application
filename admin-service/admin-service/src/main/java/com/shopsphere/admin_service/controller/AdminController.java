package com.shopsphere.admin_service.controller;

import com.shopsphere.admin_service.dto.ApiResponse;
import com.shopsphere.admin_service.dto.DashboardResponse;
import com.shopsphere.admin_service.feign.OrderClient;
import com.shopsphere.admin_service.service.AdminOrderService;
import com.shopsphere.admin_service.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminOrderService adminOrderService;
    private final DashboardService dashboardService;
    private final OrderClient orderClient;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard fetched successfully", dashboardService.getDashboard()));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<Object>> getAllOrders() {
        return ResponseEntity.ok(ApiResponse.success("Orders fetched successfully", adminOrderService.getAllOrders().getData()));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<Object>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(ApiResponse.success("Order status updated", adminOrderService.updateOrderStatus(id, body.get("status")).getData()));
    }

    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<Object>> getReports() {
        return ResponseEntity.ok(ApiResponse.success("Reports fetched successfully", orderClient.getAllOrders().getData()));
    }
}