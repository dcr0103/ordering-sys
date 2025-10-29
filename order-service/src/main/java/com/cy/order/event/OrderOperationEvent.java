package com.cy.order.event;

import com.cy.order.dto.OrderDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

/**
 * 订单操作事件
 * 用于在订单操作（创建、更新、取消等）后通知相关组件进行后续处理
 */
@Getter
public class OrderOperationEvent extends ApplicationEvent {

    /**
     * 事件类型
     */
    private final EventTypeEnum eventType;

    /**
     * 订单信息
     */
    private final OrderDto order;

    /**
     * 事件发生时间
     */
    private final LocalDateTime eventTime;

    /**
     * 操作者ID（可选）
     */
    private final String operatorId;

    /**
     * 操作描述（可选）
     */
    private final String operationDescription;

    /**
     * 创建订单操作事件
     * 
     * @param source 事件源
     * @param eventType 事件类型
     * @param order 订单信息
     */
    public OrderOperationEvent(Object source, EventTypeEnum eventType, OrderDto order) {
        this(source, eventType, order, null, null);
    }

    /**
     * 创建订单操作事件
     * 
     * @param source 事件源
     * @param eventType 事件类型
     * @param order 订单信息
     * @param operatorId 操作者ID
     * @param operationDescription 操作描述
     */
    public OrderOperationEvent(Object source, EventTypeEnum eventType, OrderDto order, 
                              String operatorId, String operationDescription) {
        super(source);
        this.eventType = eventType;
        this.order = order;
        this.eventTime = LocalDateTime.now();
        this.operatorId = operatorId;
        this.operationDescription = operationDescription;
    }
}