package com.shopsphere.order_service.controller;

import com.shopsphere.order_service.dto.*;
import com.shopsphere.order_service.enums.OrderStatus;
import com.shopsphere.order_service.service.OrderService;
import com.shopsphere.order_service.dto.ApiResponse;
import com.shopsphere.order_service.dto.OrderResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout/start")
    public ResponseEntity<ApiResponse<OrderResponse>> startCheckout(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Checkout started", orderService.startCheckout(userId, request)));
    }

    @PostMapping("/place")
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody PlaceOrderRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Order placed successfully", orderService.placeOrder(userId, request.getOrderId())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Order fetched successfully", orderService.getOrderById(userId, id)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Orders fetched successfully", orderService.getMyOrders(userId)));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        return ResponseEntity.ok(ApiResponse.success(
                "All orders fetched", orderService.getAllOrders()));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        OrderStatus status = OrderStatus.valueOf(body.get("status"));

        return ResponseEntity.ok(ApiResponse.success(
                "Order status updated", orderService.updateOrderStatus(id, status)));
    }
}