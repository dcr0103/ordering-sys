package com.cy.order.listener;

import com.cy.order.dto.OrderDto;
import com.cy.order.event.EventTypeEnum;
import com.cy.order.event.OrderOperationEvent;
import com.cy.order.websocket.AppWebSocketEndpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * 用户通知监听器
 * 负责处理订单操作后的用户通知相关逻辑
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class UserNotificationListener {

    /**
     * 监听订单操作事件，向用户发送通知
     * 
     * @param event 订单操作事件
     */
    @EventListener(OrderOperationEvent.class)
    public void handleOrderOperationEvent(OrderOperationEvent event) {
        // 只处理订单更新事件
        if (event.getEventType() != EventTypeEnum.ORDER_UPDATED) {
            return;
        }
        
        OrderDto order = event.getOrder();
        log.info("收到订单更新事件，准备通过WebSocket向用户发送通知，订单ID: {}, 用户ID: {}",
                 order.getId(), order.getUserId());
        
        try {
            // 构建WebSocket通知消息
            String notification = "{"
                    + "\"type\": \"order_created\","
                    + "\"orderId\": \"" + order.getId() + "\","
                    + "\"orderNumber\": \"" + order.getOrderNumber() + "\","
                    + "\"message\": \"您的订单已更新成功，订单号：" + order.getOrderNumber() + "\","
                    + "\"status\": \"PENDING\","
                    + "\"timestamp\": " + Instant.now().toEpochMilli()
                    + "}";
            
            // 通过WebSocket发送通知给用户
            AppWebSocketEndpoint.sendMessageToUser(order.getUserId(), notification);
            
            log.info("用户通知已通过WebSocket发送，订单ID: {}", order.getId());
            
        } catch (Exception e) {
            log.error("发送用户WebSocket通知失败，订单ID: {}", order.getId(), e);
            // 这里可以添加重试逻辑或失败记录
        }
    }
}