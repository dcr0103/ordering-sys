package com.cy.order.listener;

import com.cy.order.dto.OrderDto;
import com.cy.order.event.EventTypeEnum;
import com.cy.order.event.OrderOperationEvent;
import com.cy.order.mq.FanoutRabbitConfig;
import com.cy.order.websocket.AppWebSocketEndpoint;
import com.cy.order.websocket.StoreWebSocketEndpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * 支付通知监听器
 * 负责处理订单支付相关的通知逻辑
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentNotificationListener {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 监听订单操作事件，处理支付相关通知
     * 
     * @param event 订单操作事件
     */
    @Async
    @EventListener(OrderOperationEvent.class)
    public void handlePaymentEvent(OrderOperationEvent event) {
        OrderDto order = event.getOrder();

        switch (event.getEventType()) {
            case ORDER_PAID:
                handleOrderPaid(order);
                break;
            default:
                // 不处理其他类型的事件
                break;
        }
    }

    /**
     * 处理订单支付事件
     * 
     * @param order 订单信息
     */
    private void handleOrderPaid(OrderDto order) {
        log.info("收到订单支付事件，准备通过WebSocket向用户发送通知，订单ID: {}, 用户ID: {}",
                 order.getId(), order.getUserId());
        
        try {
            // 构建WebSocket通知消息
            String notification = "{"
                    + "\"type\": \"order_paid\","
                    + "\"orderId\": \"" + order.getId() + "\","
                    + "\"orderNumber\": \"" + order.getOrderNumber() + "\","
                    + "\"message\": \"您的订单已支付成功，订单号：" + order.getOrderNumber() + "\","
                    + "\"status\": \"PAID\","
                    + "\"timestamp\": " + Instant.now().toEpochMilli()
                    + "}";
            
            // 通过WebSocket发送通知给用户
            AppWebSocketEndpoint.sendMessageToUser(order.getUserId(), notification);

            // 通过WebSocket发送通知给门店
            StoreWebSocketEndpoint.sendMessageToStore(order.getStoreId(), notification);

            // 发送订单支付成功消息到MQ
            rabbitTemplate.convertAndSend(FanoutRabbitConfig.FANOUT_EXCHANGE, "", order);

            log.info("支付成功通知已通过 websockert 和 mq发送，订单ID: {}", order.getId());
            
        } catch (Exception e) {
            log.error("发送支付成功通知失败，订单ID: {}", order.getId(), e);
        }
    }
}