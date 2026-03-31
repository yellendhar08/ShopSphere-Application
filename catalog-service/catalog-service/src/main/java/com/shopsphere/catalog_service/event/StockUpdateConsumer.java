package com.shopsphere.catalog_service.event;

import com.shopsphere.catalog_service.config.RabbitMQConfig;
import com.shopsphere.catalog_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockUpdateConsumer {

    private final ProductService productService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_CONFIRMATION_QUEUE)
    public void handleOrderConfirmation(OrderConfirmationEvent event) {
        event.getItems().forEach(item ->
                productService.updateStock(item.getProductId(), item.getQuantity())
        );
    }
}