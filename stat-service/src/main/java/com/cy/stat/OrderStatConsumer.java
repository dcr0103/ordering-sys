package com.cy.stat;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * 订单统计消费者
 * 负责消费订单统计相关的消息并进行处理
 */
@Component
@Slf4j
public class OrderStatConsumer {

    @RabbitListener(queues = "order.notify.queue.stat")
    public void handleOrder(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            log.info("【消费者 统计】收到订单通知: {}", message);
            channel.basicAck(tag, false);
        }catch (Exception e) {
            log.error("【消费者 统计】处理订单通知失败: {}", message, e);
        }
    }
}