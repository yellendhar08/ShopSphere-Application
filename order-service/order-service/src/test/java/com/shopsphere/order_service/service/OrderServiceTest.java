package com.shopsphere.order_service.service;

import com.shopsphere.order_service.dto.*;
import com.shopsphere.order_service.entity.*;
import com.shopsphere.order_service.enums.OrderStatus;
import com.shopsphere.order_service.enums.PaymentMode;
import com.shopsphere.order_service.event.OrderEventPublisher;
import com.shopsphere.order_service.exception.*;
import com.shopsphere.order_service.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartService cartService;
    @Mock
    private OrderEventPublisher orderEventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    void startCheckout_success() {
        Long userId = 1L;

        CartItem item = CartItem.builder()
                .productId(1L)
                .productName("Laptop")
                .price(1000.0)
                .quantity(2)
                .build();

        Cart cart = Cart.builder()
                .userId(userId)
                .items(List.of(item))
                .build();

        CheckoutRequest request = new CheckoutRequest();
        request.setShippingAddress("Hyderabad");

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.startCheckout(userId, request);

        assertNotNull(response);
        assertEquals(2000.0, response.getTotalAmount());
    }

    @Test
    void startCheckout_cartEmpty_shouldThrow() {
        Long userId = 1L;

        Cart cart = Cart.builder().userId(userId).items(new ArrayList<>()).build();

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        assertThrows(CartNotFoundException.class,
                () -> orderService.startCheckout(userId, new CheckoutRequest()));
    }

    @Test
    void getOrderById_success() {
        Order order = Order.builder().id(1L).userId(1L).build();

        when(orderRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L, 1L);

        assertNotNull(response);
    }

    @Test
    void getOrderById_notFound() {
        when(orderRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.getOrderById(1L, 1L));
    }

    @Test
    void placeOrder_success() {
        Long userId = 1L;

        OrderItem item = OrderItem.builder()
                .productId(1L)
                .quantity(2)
                .price(100.0)
                .productName("Laptop")
                .build();

        Order order = Order.builder()
                .id(1L)
                .userId(userId)
                .status(OrderStatus.PAID)
                .paymentMode(PaymentMode.COD)
                .shippingAddress("Hyderabad")
                .totalAmount(200.0)
                .items(List.of(item))
                .build();

        when(orderRepository.findByIdAndUserId(1L, userId)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.placeOrder(userId, 1L,  "test@test.com");
        assertNotNull(response);
        verify(orderEventPublisher, times(1))
                .publishOrderConfirmation(any());
        verify(cartService).clearCart(userId);
    }

    @Test
    void placeOrder_notPaid_shouldThrow() {
        Order order = Order.builder()
                .id(1L)
                .userId(1L)
                .status(OrderStatus.CHECKOUT)
                .build();

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStatusException.class, () -> orderService.placeOrder(1L, 1L, "test@test.com"));
    }

    @Test
    void getMyOrders_success() {
        Long userId = 1L;
        Order order1 = Order.builder().id(1L).userId(userId).build();
        Order order2 = Order.builder().id(2L).userId(userId).build();

        when(orderRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(order1, order2));

        List<OrderResponse> result = orderService.getMyOrders(userId);
        assertEquals(2, result.size());
        verify(orderRepository).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void updateOrderStatus_success() {
        Order order = Order.builder().id(1L).status(OrderStatus.CHECKOUT).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.updateOrderStatus(1L, OrderStatus.PAID);

        assertEquals(OrderStatus.PAID, response.getStatus());
    }
}