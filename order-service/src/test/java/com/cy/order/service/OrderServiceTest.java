package com.cy.order.service;

import com.cy.order.dto.OrderDto;
import com.cy.order.dto.OrderItemDto;
import com.cy.order.dto.OrderRequestDto;
import com.cy.order.enums.OrderStatusEnum;
import com.cy.order.event.EventTypeEnum;
import com.cy.order.event.OrderOperationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<OrderOperationEvent> eventCaptor;

    private OrderRequestDto orderRequestDto;
    private OrderItemDto orderItemDto;

    @BeforeEach
    void setUp() {
        orderItemDto = new OrderItemDto("1", "Product 1", 2, new BigDecimal("10.00"));
        List<OrderItemDto> items = Arrays.asList(orderItemDto);
        orderRequestDto = new OrderRequestDto();
        orderRequestDto.setUserId("user1");
        orderRequestDto.setStoreId("store1");
        orderRequestDto.setAmount(new BigDecimal("20.00"));
        orderRequestDto.setItems(items);
    }

    @Test
    void createOrder_ShouldCreateOrderAndPublishEvent() {
        // When
        String orderId = orderService.createOrder(orderRequestDto);

        // Then
        assertNotNull(orderId);
        OrderDto order = orderService.getOrderById(orderId);
        assertNotNull(order);
        assertEquals("user1", order.getUserId());
        assertEquals("store1", order.getStoreId());
        assertEquals(new BigDecimal("20.00"), order.getAmount());
        assertEquals(OrderStatusEnum.CREATED, order.getStatus());
        assertEquals(1, order.getItems().size());
        assertNotNull(order.getOrderNumber());
        assertNotNull(order.getCreateTime());
        
        // Verify event was published
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        OrderOperationEvent publishedEvent = eventCaptor.getValue();
        assertEquals(EventTypeEnum.ORDER_CREATED, publishedEvent.getEventType());
        assertEquals(orderId, publishedEvent.getOrder().getId());
    }

    @Test
    void getOrderById_WithExistingOrder_ShouldReturnOrder() {
        // Given
        String orderId = orderService.createOrder(orderRequestDto);

        // When
        OrderDto order = orderService.getOrderById(orderId);

        // Then
        assertNotNull(order);
        assertEquals(orderId, order.getId());
        assertEquals("user1", order.getUserId());
        assertEquals("store1", order.getStoreId());
        assertEquals(new BigDecimal("20.00"), order.getAmount());
        assertEquals(OrderStatusEnum.CREATED, order.getStatus());
    }

    @Test
    void getOrderById_WithNonExistingOrder_ShouldReturnNull() {
        // When
        OrderDto order = orderService.getOrderById("non-existing-id");

        // Then
        assertNull(order);
    }
}