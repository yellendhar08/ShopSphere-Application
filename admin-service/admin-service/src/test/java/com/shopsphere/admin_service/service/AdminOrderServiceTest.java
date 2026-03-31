package com.shopsphere.admin_service.service;
import com.shopsphere.admin_service.dto.ApiResponse;
import com.shopsphere.admin_service.feign.OrderClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminOrderServiceTest {

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private AdminOrderService adminOrderService;

    @Test
    void getAllOrders_success() {
        ApiResponse<Object> mockResponse = new ApiResponse<>();
        mockResponse.setMessage("Success");

        when(orderClient.getAllOrders()).thenReturn(mockResponse);

        ApiResponse<Object> response = adminOrderService.getAllOrders();

        assertNotNull(response);
        assertEquals("Success", response.getMessage());

        verify(orderClient, times(1)).getAllOrders();
    }

    @Test
    void updateOrderStatus_success() {
        Long orderId = 1L;
        String status = "PAID";

        ApiResponse<Object> mockResponse = new ApiResponse<>();
        mockResponse.setMessage("Updated");

        when(orderClient.updateOrderStatus(eq(orderId), anyMap()))
                .thenReturn(mockResponse);

        ApiResponse<Object> response =
                adminOrderService.updateOrderStatus(orderId, status);

        assertNotNull(response);
        assertEquals("Updated", response.getMessage());

        verify(orderClient).updateOrderStatus(orderId, Map.of("status", status));
    }
}