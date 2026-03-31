package com.shopsphere.admin_service.service;

import com.shopsphere.admin_service.dto.ApiResponse;
import com.shopsphere.admin_service.feign.OrderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminOrderService {

    private final OrderClient orderClient;

    public ApiResponse<Object> getAllOrders() {
        return orderClient.getAllOrders();
    }

    public ApiResponse<Object> updateOrderStatus(Long id, String status) {
        return orderClient.updateOrderStatus(id, Map.of("status", status));
    }
}
