package com.shopsphere.order_service.event;

import com.shopsphere.order_service.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishOrderConfirmation(OrderConfirmationEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_CONFIRMATION_ROUTING_KEY,
                event
        );
        System.out.println("Published OrderConfirmationEvent for order: " + event.getOrderId());
    }
}