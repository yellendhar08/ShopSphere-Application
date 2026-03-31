package com.shopsphere.admin_service.service;

import com.shopsphere.admin_service.dto.ApiResponse;
import com.shopsphere.admin_service.dto.DashboardResponse;
import com.shopsphere.admin_service.feign.CatalogClient;
import com.shopsphere.admin_service.feign.OrderClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private OrderClient orderClient;

    @Mock
    private CatalogClient catalogClient;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getDashboard_success() {

        Map<String, Object> order1 = new HashMap<>();
        order1.put("status", "DELIVERED");
        order1.put("totalAmount", 100.0);

        Map<String, Object> order2 = new HashMap<>();
        order2.put("status", "PAID");
        order2.put("totalAmount", 200.0);

        List<Map<String, Object>> ordersList = List.of(order1, order2);

        ApiResponse<Object> ordersResp = new ApiResponse<>();
        ordersResp.setData(ordersList);

        Map<String, Object> productData = new HashMap<>();
        productData.put("totalElements", 50);

        ApiResponse<Object> productsResp = new ApiResponse<>();
        productsResp.setData(productData);

        when(orderClient.getAllOrders()).thenReturn(ordersResp);
        when(catalogClient.getAllProducts()).thenReturn(productsResp);

        DashboardResponse response = dashboardService.getDashboard();

        assertNotNull(response);

        assertEquals(2, response.getTotalOrders());
        assertEquals(50, response.getTotalProducts());
        assertEquals(100.0, response.getTotalRevenue());
        assertEquals(1, response.getPendingOrders());
        assertEquals(1, response.getDeliveredOrders());
    }
}