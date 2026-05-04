package com.shopsphere.order_service.service;

import com.shopsphere.order_service.dto.OrderResponse;
import com.shopsphere.order_service.dto.PaymentRequest;
import com.shopsphere.order_service.entity.Order;
import com.shopsphere.order_service.enums.OrderStatus;
import com.shopsphere.order_service.enums.PaymentMode;
import com.shopsphere.order_service.exception.InvalidOrderStatusException;
import com.shopsphere.order_service.exception.OrderNotFoundException;
import com.shopsphere.order_service.exception.PaymentFailedException;
import com.shopsphere.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public OrderResponse processPayment(Long userId, PaymentRequest request) {

        Order order = orderRepository.findByIdAndUserId(request.getOrderId(), userId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.CHECKOUT) {
            throw new InvalidOrderStatusException("Order is not in checkout state");
        }

        if (request.getPaymentMode() == PaymentMode.COD) {
            order.setPaymentMode(PaymentMode.COD);
            order.setStatus(OrderStatus.PLACED);
            orderRepository.save(order);
            return orderService.getOrderById(userId, order.getId());
        }

        if (request.getPaymentMode() == PaymentMode.PREPAID) {
            order.setPaymentMode(PaymentMode.PREPAID);
            order.setStatus(OrderStatus.PAID);
            orderRepository.save(order);
            return orderService.getOrderById(userId, order.getId());
        }

        throw new PaymentFailedException("Unsupported payment mode. Use COD or PREPAID.");
    }
}