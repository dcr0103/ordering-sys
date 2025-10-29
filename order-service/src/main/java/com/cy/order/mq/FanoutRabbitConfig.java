package com.cy.order.mq;


import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;

/**
 * 广播交换机
 * 一条消息被多个消费者都收到
 */
@Configuration
public class FanoutRabbitConfig {

    public static final String FANOUT_EXCHANGE = "order.fanout.exchange";

    // 消费者 统计 的队列
    @Bean
    public Queue orderNotifyQueueStat() {
        return QueueBuilder.durable("order.notify.queue.stat").build();
    }

    // 消费者 crm 的队列
    @Bean
    public Queue orderNotifyQueueCrm() {
        return QueueBuilder.durable("order.notify.queue.crm").build();
    }

    // 消费者 库存 的队列
    @Bean
    public Queue orderNotifyQueueInventory() {
        return QueueBuilder.durable("order.notify.queue.inventory").build();
    }

    // Fanout Exchange（广播交换机）
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    // 将所有队列绑定到 Fanout Exchange（无需 routing key）
    @Bean
    public Binding bindingStat() {
        return BindingBuilder.bind(orderNotifyQueueStat()).to(fanoutExchange());
    }

    @Bean
    public Binding bindingCrm() {
        return BindingBuilder.bind(orderNotifyQueueCrm()).to(fanoutExchange());
    }


    @Bean
    public Binding bindingInventory() {
        return BindingBuilder.bind(orderNotifyQueueInventory()).to(fanoutExchange());
    }

}