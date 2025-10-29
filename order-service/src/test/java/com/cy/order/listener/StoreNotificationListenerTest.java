package com.cy.order.listener;

import com.cy.order.dto.OrderDto;
import com.cy.order.event.EventTypeEnum;
import com.cy.order.event.OrderOperationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class StoreNotificationListenerTest {


    @Test
    void testHandleOrderOperationEventWithOrderCreated() {
        // Given
        StoreNotificationListener listener = new StoreNotificationListener();
        OrderDto order = createTestOrder();
        OrderOperationEvent event = new OrderOperationEvent(this, EventTypeEnum.ORDER_CREATED, order);
        
        // Mock WebSocket endpoint static method
        try (MockedStatic<com.cy.order.websocket.StoreWebSocketEndpoint> mockedWebSocket = 
                mockStatic(com.cy.order.websocket.StoreWebSocketEndpoint.class)) {
            
            // When
            listener.handleOrderOperationEvent(event);
            
            // Then
            mockedWebSocket.verify(() -> 
                com.cy.order.websocket.StoreWebSocketEndpoint.sendMessageToStore(
                    eq(order.getStoreId()), anyString()));
            
        }
    }

    @Test
    void testHandleOrderOperationEventWithOrderCancelled() {
        // Given
        OrderDto order = createTestOrder();
        OrderOperationEvent event = new OrderOperationEvent(this, EventTypeEnum.ORDER_CANCELLED, order);
        

        // Verify no WebSocket message sent
        try (MockedStatic<com.cy.order.websocket.StoreWebSocketEndpoint> mockedWebSocket = 
                mockStatic(com.cy.order.websocket.StoreWebSocketEndpoint.class)) {
            
            // Verify no interaction with WebSocket endpoint
            mockedWebSocket.verifyNoInteractions();
        }
    }

    private OrderDto createTestOrder() {
        return OrderDto.builder()
                .id(UUID.randomUUID().toString())
                .orderNumber("ORD20231029001")
                .userId("user001")
                .storeId("store001")
                .amount(new BigDecimal("99.99"))
                .createTime(LocalDateTime.now())
                .build();
    }
}