package com.cy.order.listener;

import com.cy.order.dto.OrderDto;
import com.cy.order.event.EventTypeEnum;
import com.cy.order.event.OrderOperationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserNotificationListenerTest {

    @Test
    void testHandleOrderOperationEventWithOrderCreated() {
        // Given
        UserNotificationListener listener = new UserNotificationListener();
        OrderDto order = createTestOrder();
        OrderOperationEvent event = new OrderOperationEvent(this, EventTypeEnum.ORDER_CREATED, order);
        
        // Mock WebSocket endpoint static method
        try (MockedStatic<com.cy.order.websocket.AppWebSocketEndpoint> mockedWebSocket = 
                mockStatic(com.cy.order.websocket.AppWebSocketEndpoint.class)) {
            
            // When
            listener.handleOrderOperationEvent(event);
            
            // Then
            mockedWebSocket.verify(() -> 
                com.cy.order.websocket.AppWebSocketEndpoint.sendMessageToUser(
                    eq(order.getUserId()), anyString()));
        }
    }

    @Test
    void testHandleOrderOperationEventWithOrderCancelled() {
        // Given
        UserNotificationListener listener = new UserNotificationListener();
        OrderDto order = createTestOrder();
        OrderOperationEvent event = new OrderOperationEvent(this, EventTypeEnum.ORDER_CANCELLED, order);
        
        // When & Then
        assertDoesNotThrow(() -> listener.handleOrderOperationEvent(event));
    }

    private OrderDto createTestOrder() {
        return OrderDto.builder()
                .id(UUID.randomUUID().toString())
                .orderNumber("ORD20231029001")
                .userId("user001")
                .storeId("store001")
                .createTime(LocalDateTime.now())
                .build();
    }
}