package com.shopsphere.notification_service.service;

import com.shopsphere.notification_service.dto.OrderPlacedEvent;
import com.shopsphere.notification_service.dto.OrderItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to ShopSphere!");
            message.setText(
                "Hi " + name + ",\n\n" +
                "Welcome to ShopSphere! Your account has been created successfully.\n\n" +
                "We're excited to have you with us. Explore a wide range of products and enjoy a seamless shopping experience.\n\n" +
                "Thank you for joining us.\n" +
                "ShopSphere Team"
            );
            mailSender.send(message);
            log.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendOrderPlacedEmail(OrderPlacedEvent event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(event.getUserEmail());
            message.setSubject("Order #" + event.getOrderId() + " Confirmed — ShopSphere");

            StringBuilder text = new StringBuilder();
            text.append("Hi ").append(event.getUserName()).append(",\n\n");
            text.append("Your order has been placed successfully!\n\n");
            text.append("Order ID: #").append(event.getOrderId()).append("\n");
            text.append("Total Amount: ₹").append(event.getTotalAmount()).append("\n");
            text.append("Shipping Address: ").append(event.getShippingAddress()).append("\n\n");
            text.append("Items Ordered:\n");
            if (event.getItems() != null) {
                for (OrderItemDto item : event.getItems()) {
                    text.append("- ").append(item.getProductName())
                        .append(" x").append(item.getQuantity())
                        .append(" = ₹").append(item.getPrice()).append("\n");
                }
            }
            text.append("\nThank you for shopping with us!\n");
            text.append("ShopSphere Team");

            message.setText(text.toString());
            mailSender.send(message);
            log.info("Order confirmation email sent to: {}", event.getUserEmail());
        } catch (Exception e) {
            log.error("Failed to send order email to {}: {}", event.getUserEmail(), e.getMessage());
        }
    }
}
