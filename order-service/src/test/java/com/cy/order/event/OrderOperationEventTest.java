package com.cy.order.event;

import com.cy.order.dto.OrderDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderOperationEventTest {

    @Test
    void testCreateOrderOperationEventWithMinimalParameters() {
        // Given
        Object source = new Object();
        EventTypeEnum eventType = EventTypeEnum.ORDER_CREATED;
        OrderDto order = createTestOrder();

        // When
        OrderOperationEvent event = new OrderOperationEvent(source, eventType, order);

        // Then
        assertEquals(source, event.getSource());
        assertEquals(eventType, event.getEventType());
        assertEquals(order, event.getOrder());
        assertNotNull(event.getEventTime());
        assertNull(event.getOperatorId());
        assertNull(event.getOperationDescription());
    }

    @Test
    void testCreateOrderOperationEventWithAllParameters() {
        // Given
        Object source = new Object();
        EventTypeEnum eventType = EventTypeEnum.ORDER_CANCELLED;
        OrderDto order = createTestOrder();
        String operatorId = "user123";
        String operationDescription = "用户取消订单";

        // When
        OrderOperationEvent event = new OrderOperationEvent(source, eventType, order, operatorId, operationDescription);

        // Then
        assertEquals(source, event.getSource());
        assertEquals(eventType, event.getEventType());
        assertEquals(order, event.getOrder());
        assertNotNull(event.getEventTime());
        assertEquals(operatorId, event.getOperatorId());
        assertEquals(operationDescription, event.getOperationDescription());
    }

    @Test
    void testEventTimeIsSetOnCreation() {
        // Given
        Object source = new Object();
        EventTypeEnum eventType = EventTypeEnum.ORDER_CREATED;
        OrderDto order = createTestOrder();
        LocalDateTime beforeCreation = LocalDateTime.now();

        // When
        OrderOperationEvent event = new OrderOperationEvent(source, eventType, order);
        LocalDateTime afterCreation = LocalDateTime.now();

        // Then
        assertNotNull(event.getEventTime());
        assertTrue(event.getEventTime().isAfter(beforeCreation) || event.getEventTime().isEqual(beforeCreation));
        assertTrue(event.getEventTime().isBefore(afterCreation) || event.getEventTime().isEqual(afterCreation));
    }

    private OrderDto createTestOrder() {
        return OrderDto.builder()
                .id(UUID.randomUUID().toString())
                .orderNumber("ORD20231029001")
                .userId("user001")
                .storeId("store001")
                .build();
    }
}