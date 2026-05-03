package com.shopsphere.notification_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Queues
    public static final String SIGNUP_QUEUE = "notification.signup";
    public static final String ORDER_PLACED_QUEUE = "notification.order.placed";

    // Exchange
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";

    // Routing Keys
    public static final String SIGNUP_ROUTING_KEY = "user.signup";
    public static final String ORDER_PLACED_ROUTING_KEY = "order.placed";

    @Bean
    public Queue signupQueue() {
        return new Queue(SIGNUP_QUEUE, true);
    }

    @Bean
    public Queue orderPlacedQueue() {
        return new Queue(ORDER_PLACED_QUEUE, true);
    }

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Binding signupBinding() {
        return BindingBuilder.bind(signupQueue())
                .to(notificationExchange())
                .with(SIGNUP_ROUTING_KEY);
    }

    @Bean
    public Binding orderPlacedBinding() {
        return BindingBuilder.bind(orderPlacedQueue())
                .to(notificationExchange())
                .with(ORDER_PLACED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
