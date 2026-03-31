package com.shopsphere.admin_service.feign;

import com.shopsphere.admin_service.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "order-service")
public interface OrderClient {

    @GetMapping("/orders/all")
    ApiResponse<Object> getAllOrders();

    @PutMapping("/orders/{id}/status")
    ApiResponse<Object> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body);
}
