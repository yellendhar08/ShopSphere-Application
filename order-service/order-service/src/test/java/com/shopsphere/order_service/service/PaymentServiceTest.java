package com.shopsphere.order_service.service;

import com.shopsphere.order_service.dto.OrderResponse;
import com.shopsphere.order_service.dto.PaymentRequest;
import com.shopsphere.order_service.entity.Order;
import com.shopsphere.order_service.enums.OrderStatus;
import com.shopsphere.order_service.enums.PaymentMode;
import com.shopsphere.order_service.exception.InvalidOrderStatusException;
import com.shopsphere.order_service.exception.PaymentFailedException;
import com.shopsphere.order_service.repository.OrderRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

        @Mock
        private OrderRepository orderRepository;
        @Mock
        private OrderService orderService;

        @InjectMocks
        private PaymentService paymentService;

        @Test
        void processPayment_cod_success() {
                Long userId = 1L;

                Order order = Order.builder()
                                .id(1L)
                                .userId(userId)
                                .status(OrderStatus.CHECKOUT)
                                .build();

                PaymentRequest request = new PaymentRequest();
                request.setOrderId(1L);
                request.setPaymentMode(PaymentMode.COD);

                when(orderRepository.findByIdAndUserId(1L, userId))
                                .thenReturn(Optional.of(order));

                when(orderService.getOrderById(userId, 1L))
                                .thenReturn(new OrderResponse());

                OrderResponse response = paymentService.processPayment(userId, request);

                assertEquals(OrderStatus.PAID, order.getStatus());
                assertNotNull(response);
        }

        @Test
        void processPayment_upi_shouldThrow() {
                Long userId = 1L;

                Order order = Order.builder()
                                .id(1L)
                                .userId(userId)
                                .status(OrderStatus.CHECKOUT)
                                .build();

                PaymentRequest request = new PaymentRequest();
                request.setOrderId(1L);
                request.setPaymentMode(PaymentMode.PREPAID);

                when(orderRepository.findByIdAndUserId(1L, userId))
                                .thenReturn(Optional.of(order));

                assertThrows(PaymentFailedException.class,
                                () -> paymentService.processPayment(userId, request));

                assertEquals(OrderStatus.CHECKOUT, order.getStatus());
        }

        @Test
        void processPayment_invalidStatus_shouldThrow() {
                Long userId = 1L;

                Order order = Order.builder()
                                .id(1L)
                                .userId(userId)
                                .status(OrderStatus.PAID)
                                .build();

                PaymentRequest request = new PaymentRequest();
                request.setOrderId(1L);
                request.setPaymentMode(PaymentMode.COD);

                when(orderRepository.findByIdAndUserId(1L, userId))
                                .thenReturn(Optional.of(order));

                assertThrows(InvalidOrderStatusException.class,
                                () -> paymentService.processPayment(userId, request));
        }
}