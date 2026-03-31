package com.shopsphere.catalog_service.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemEvent {
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double price;
}