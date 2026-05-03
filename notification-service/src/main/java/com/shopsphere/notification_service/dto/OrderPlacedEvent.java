package com.shopsphere.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

public class OrderPlacedEvent {
    private Long orderId;
    private String userEmail;
    private String userName;
    private Double totalAmount;
    private String shippingAddress;
    private List<OrderItemDto> items;

    public OrderPlacedEvent() {}

    public OrderPlacedEvent(Long orderId, String userEmail, String userName, Double totalAmount, String shippingAddress, List<OrderItemDto> items) {
        this.orderId = orderId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.items = items;
    }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public List<OrderItemDto> getItems() { return items; }
    public void setItems(List<OrderItemDto> items) { this.items = items; }
}
