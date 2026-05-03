package com.shopsphere.notification_service.listener;

import com.shopsphere.notification_service.config.RabbitMQConfig;
import com.shopsphere.notification_service.dto.OrderPlacedEvent;
import com.shopsphere.notification_service.dto.SignupEvent;
import com.shopsphere.notification_service.service.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);
    
    private final EmailService emailService;

    public NotificationListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.SIGNUP_QUEUE)
    public void handleSignup(SignupEvent event) {
        log.info("Received signup event for: {}", event.getEmail());
        emailService.sendWelcomeEmail(event.getEmail(), event.getName());
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_PLACED_QUEUE)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received order placed event for order: {}", event.getOrderId());
        emailService.sendOrderPlacedEmail(event);
    }
}
