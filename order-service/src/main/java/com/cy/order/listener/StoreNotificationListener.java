package com.cy.order.listener;

import com.cy.order.dto.OrderDto;
import com.cy.order.event.EventTypeEnum;
import com.cy.order.event.OrderOperationEvent;
import com.cy.order.websocket.StoreWebSocketEndpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * 门店通知监听器
 * 负责处理订单操作后的门店通知相关逻辑，通过WebSocket发送通知
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class StoreNotificationListener {


    /**
     * 监听订单操作事件，向门店发送通知
     * 
     * @param event 订单操作事件
     */
    @Async
    @EventListener(OrderOperationEvent.class)
    public void handleOrderOperationEvent(OrderOperationEvent event) {
        // 只处理订单创建事件
        if (event.getEventType() != EventTypeEnum.ORDER_CREATED) {
            return;
        }
        
        OrderDto order = event.getOrder();
        log.info("收到订单创建事件，准备通过WebSocket向门店发送通知，订单ID: {}, 门店ID: {}", 
                 order.getId(), order.getStoreId());
        
        try {
            // 构建门店WebSocket通知消息
            String notification = "{"
                    + "\"type\": \"order_created\","
                    + "\"orderId\": \"" + order.getId() + "\","
                    + "\"orderNumber\": \"" + order.getOrderNumber() + "\","
                    + "\"message\": \"新订单已创建，请及时处理\","
                    + "\"status\": \"NEW\","
                    + "\"timestamp\": " + Instant.now().toEpochMilli()
                    + "}";

            // 通过WebSocket发送通知给门店
            StoreWebSocketEndpoint.sendMessageToStore(order.getStoreId(), notification);
            log.info("门店通知已通过WebSocket发送，订单ID: {}", order.getId());
        } catch (Exception e) {
            log.error("发送门店WebSocket通知失败，订单ID: {}", order.getId(), e);
            // 这里可以添加重试逻辑或失败记录
        }
    }


}