package com.cy.order.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventTypeEnumTest {

    @Test
    void testOrderCreatedEventType() {
        EventTypeEnum eventType = EventTypeEnum.ORDER_CREATED;
        assertEquals("order_created", eventType.getCode());
        assertEquals("订单创建事件", eventType.getDescription());
    }

    @Test
    void testOrderCancelledEventType() {
        EventTypeEnum eventType = EventTypeEnum.ORDER_CANCELLED;
        assertEquals("order_cancelled", eventType.getCode());
        assertEquals("订单取消事件", eventType.getDescription());
    }

    @Test
    void testPaymentSuccessEventType() {
        EventTypeEnum eventType = EventTypeEnum.PAYMENT_SUCCESS;
        assertEquals("payment_success", eventType.getCode());
        assertEquals("支付成功事件", eventType.getDescription());
    }

    @Test
    void testFromCodeWithExistingCode() {
        EventTypeEnum eventType = EventTypeEnum.fromCode("order_created");
        assertEquals(EventTypeEnum.ORDER_CREATED, eventType);
    }

    @Test
    void testFromCodeWithNonExistingCode() {
        EventTypeEnum eventType = EventTypeEnum.fromCode("non_existing_code");
        assertNull(eventType);
    }

    @Test
    void testAllEventTypesHaveUniqueCodes() {
        EventTypeEnum[] eventTypes = EventTypeEnum.values();
        for (int i = 0; i < eventTypes.length; i++) {
            for (int j = i + 1; j < eventTypes.length; j++) {
                assertNotEquals(eventTypes[i].getCode(), eventTypes[j].getCode(), 
                    "Duplicate code found: " + eventTypes[i].getCode());
            }
        }
    }
}