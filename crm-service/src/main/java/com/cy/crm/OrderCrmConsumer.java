package com.cy.crm;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 订单CRM消费者
 * 负责消费CRM相关的消息并进行处理
 */
@Component
@Slf4j
public class OrderCrmConsumer {

    @RabbitListener(queues = "order.notify.queue.crm")
    public void handleOrder(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
       try {
           log.info("【消费者 CRM】收到订单通知: {}", message);
           channel.basicAck(tag, false);
       } catch (IOException ex) {
           log.error("【消费者 CRM】处理订单通知失败: {}", message, ex);
       }
    }
}