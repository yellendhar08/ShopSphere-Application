package com.shopsphere.order_service.service;

import com.shopsphere.order_service.dto.*;
import com.shopsphere.order_service.entity.*;
import com.shopsphere.order_service.enums.OrderStatus;
import com.shopsphere.order_service.event.OrderEventPublisher;
import com.shopsphere.order_service.exception.*;
import com.shopsphere.order_service.feign.CatalogClient;
import com.shopsphere.order_service.event.OrderConfirmationEvent;
import com.shopsphere.order_service.event.OrderItemEvent;
import com.shopsphere.order_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartService cartService;
//    private final CatalogClient catalogClient;
    private final OrderEventPublisher orderEventPublisher;

    public OrderResponse startCheckout(Long userId, CheckoutRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException("Cart is empty"));

        if (cart.getItems().isEmpty())
            throw new CartNotFoundException("Cart is empty");

        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity()).sum();

        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.CHECKOUT)
                .shippingAddress(request.getShippingAddress())
                .totalAmount(total)
                .build();

        List<OrderItem> orderItems = cart.getItems().stream().map(i ->
                OrderItem.builder()
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .quantity(i.getQuantity())
                        .price(i.getPrice())
                        .order(order)
                        .build()
        ).collect(Collectors.toList());

        order.setItems(orderItems);
        return toResponse(orderRepository.save(order));
    }

    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return toResponse(order);
    }

    public List<OrderResponse> getMyOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }



    public OrderResponse placeOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PAID)
            throw new InvalidOrderStatusException("Order must be paid before placing");

//        order.getItems().forEach(item -> {
//            catalogClient.updateStock(item.getProductId(), Map.of("quantity", item.getQuantity()));
//        });

        List<OrderItemEvent> itemEvents = order.getItems().stream()
                        .map(item-> new OrderItemEvent(
                                item.getProductId(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getPrice()
                        )).toList();

        orderEventPublisher.publishOrderConfirmation(
                new OrderConfirmationEvent(
                        order.getId(),
                        order.getUserId(),
                        order.getTotalAmount(),
                        order.getShippingAddress(),
                        order.getPaymentMode().name(),
                        itemEvents));

        cartService.clearCart(userId);
        return toResponse(order);
    }



    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        order.setStatus(status);
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> items = o.getItems().stream().map(i ->
                OrderItemResponse.builder()
                        .id(i.getId())
                        .productId(i.getProductId())
                        .productName(i.getProductName())
                        .quantity(i.getQuantity())
                        .price(i.getPrice())
                        .subtotal(i.getPrice() * i.getQuantity())
                        .build()
        ).collect(Collectors.toList());

        return OrderResponse.builder()
                .id(o.getId())
                .userId(o.getUserId())
                .status(o.getStatus())
                .paymentMode(o.getPaymentMode())
                .totalAmount(o.getTotalAmount())
                .shippingAddress(o.getShippingAddress())
                .items(items)
                .createdAt(o.getCreatedAt())
                .updatedAt(o.getUpdatedAt())
                .build();
    }
}