package com.shopsphere.catalog_service.event;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderConfirmationEvent {
    private Long orderId;
    private Long userId;
    private Double totalAmount;
    private String shippingAddress;
    private String paymentMode;
    private List<OrderItemEvent> items;
}