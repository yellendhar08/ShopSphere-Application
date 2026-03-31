package com.shopsphere.order_service.dto;

import com.shopsphere.order_service.enums.PaymentMode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Payment mode is required")
    private PaymentMode paymentMode;
}