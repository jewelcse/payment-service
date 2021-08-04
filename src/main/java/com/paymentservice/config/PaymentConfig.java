package com.paymentservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PaymentConfig {
    public static final String ORDER_CREATE_QUEUE = "Order Create Queue";
    public static final String ORDER_TOPIC = "Order Topic";
    public static final String ORDER_BILLED_QUEUE = "Order Billed Queue";


    @Bean
    public Queue orderQueue() {
        return new Queue(ORDER_CREATE_QUEUE);
    }

    @Bean
    public Queue orderBilledQueue() {
        return new Queue(ORDER_BILLED_QUEUE);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(ORDER_TOPIC);
    }

    @Bean
    public Binding orderBinding(TopicExchange exchange) {
        return BindingBuilder
                .bind(orderQueue())
                .to(exchange)
                .with(orderQueue().getName());
    }

    @Bean
    public Binding orderBilledBinding(TopicExchange exchange) {
        return BindingBuilder
                .bind(orderBilledQueue())
                .to(exchange)
                .with(orderBilledQueue().getName());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate notificationRabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
