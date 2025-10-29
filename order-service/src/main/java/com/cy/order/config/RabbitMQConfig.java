package com.cy.order.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置类
 * 配置交换机、队列和绑定关系
 * 订单统计相关配置
 */
@Configuration
public class RabbitMQConfig {

    // ========= 订单统计相关配置 =========
    public static final String ORDER_STAT_EXCHANGE = "order.stat.exchange";
    public static final String ORDER_STAT_QUEUE = "order.stat.queue";
    public static final String ORDER_STAT_ROUTING_KEY = "order.stat.#";
    public static final String ORDER_STAT_DLX = "order.stat.dlx";
    public static final String ORDER_STAT_DLQ = "order.stat.dlq";

    /**
     * Jackson2JsonMessageConverter用于JSON序列化和反序列化消息
     */
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 配置RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        // 设置确认回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                // 消息成功投递到交换机
            } else {
                // 消息投递失败，可以进行重试或记录
            }
        });
        // 设置返回回调
        rabbitTemplate.setReturnsCallback(returned -> {
            // 消息投递到交换机但路由失败
        });
        return rabbitTemplate;
    }

    // ========= 订单统计相关Bean =========

    @Bean(ORDER_STAT_EXCHANGE)
    public DirectExchange orderStatExchange() {
        return new DirectExchange(ORDER_STAT_EXCHANGE, true, false);
    }

    @Bean(ORDER_STAT_QUEUE)
    public Queue orderStatQueue() {
        return QueueBuilder.durable(ORDER_STAT_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_STAT_DLX)
                .withArgument("x-message-ttl", 120000) // 120秒过期
                .build();
    }

    @Bean
    public Binding orderStatBinding(
            @Qualifier(ORDER_STAT_QUEUE) Queue queue,
            @Qualifier(ORDER_STAT_EXCHANGE) DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_STAT_ROUTING_KEY);
    }

    @Bean(ORDER_STAT_DLX)
    public DirectExchange orderStatDLX() {
        return new DirectExchange(ORDER_STAT_DLX);
    }

    @Bean(ORDER_STAT_DLQ)
    public Queue orderStatDLQ() {
        return QueueBuilder.durable(ORDER_STAT_DLQ).build();
    }

    @Bean
    public Binding orderStatDLQBinding(
            @Qualifier(ORDER_STAT_DLQ) Queue queue,
            @Qualifier(ORDER_STAT_DLX) DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ORDER_STAT_ROUTING_KEY);
    }
}