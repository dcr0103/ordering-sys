package com.cy.order.service;

import com.cy.order.dto.OrderDto;
import com.cy.order.event.EventTypeEnum;
import com.cy.order.event.OrderOperationEvent;
import com.cy.order.enums.OrderStatusEnum;
import com.cy.order.dto.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 订单服务类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final ApplicationEventPublisher eventPublisher;
    
    // 模拟订单存储，实际应用中应使用数据库
    private static final Map<String, OrderDto> ORDER_STORAGE = new HashMap<>();

    /**
     * 创建订单
     * 
     * @param orderRequest 订单请求信息
     * @return 订单ID
     */
    @Transactional
    public String createOrder(OrderRequestDto orderRequest) {
        // 生成订单ID
        String orderId = UUID.randomUUID().toString();
        
        // 构建订单信息
        OrderDto order = OrderDto.builder()
                .id(orderId)
                .orderNumber(generateOrderNumber())
                .userId(orderRequest.getUserId())
                .storeId(orderRequest.getStoreId())
                .amount(orderRequest.getAmount())
                .status(OrderStatusEnum.CREATED)
                .items(orderRequest.getItems())
                .createTime(LocalDateTime.now())
                .build();
        
        // 保存订单到模拟存储
        ORDER_STORAGE.put(orderId, order);
        log.info("创建订单: {}", order);
        
        // 发布订单创建事件
        OrderOperationEvent event = new OrderOperationEvent(this, EventTypeEnum.ORDER_CREATED, order);
        eventPublisher.publishEvent(event);
        
        return orderId;
    }

    /**
     * 生成订单号
     * 
     * @return 订单号
     */
    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis() + (int)(Math.random() * 1000);
    }

    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @return 是否成功
     */
    @Transactional
    public boolean cancelOrder(String orderId) {
        // 实际应用中需要从数据库查询订单并更新状态
        log.info("取消订单: {}", orderId);
        // TODO: 实现订单取消逻辑
        return true;
    }

    /**
     * 获取订单详情
     * 
     * @param orderId 订单ID
     * @return 订单信息
     */
    public OrderDto getOrderById(String orderId) {
        // 从模拟存储中获取订单
        OrderDto order = ORDER_STORAGE.get(orderId);
        if (order != null) {
            // 返回订单副本，避免直接修改存储中的对象
            return OrderDto.builder()
                    .id(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .userId(order.getUserId())
                    .storeId(order.getStoreId())
                    .amount(order.getAmount())
                    .status(order.getStatus())
                    .items(order.getItems())
                    .createTime(order.getCreateTime())
                    .updateTime(order.getUpdateTime())
                    .build();
        }
        return null;
    }
    
    /**
     * 门店修改订单状态
     * 
     * @param orderId 订单ID
     * @param newStatus 新状态
     * @param storeId 门店ID
     * @param userId 用户ID
     * @return 是否成功
     */
    @Transactional
    public boolean updateOrderStatus(String orderId, OrderStatusEnum newStatus, String storeId,String userId) {
        // 实际应用中需要从数据库查询订单并验证权限
        log.info("门店修改订单状态: orderId={}, newStatus={}, storeId={}, userId={}", orderId, newStatus, storeId,userId);


        OrderDto updatedOrder = OrderDto.builder()
                .id(orderId)
                .orderNumber("10")
                .userId(userId)
                .storeId(storeId)
                .amount(BigDecimal.valueOf(100))
                .status(newStatus).build();

        // 更新模拟存储中的订单
        ORDER_STORAGE.put(orderId, updatedOrder);
        log.info("订单状态已更新: orderId={}, newStatus={}", orderId, newStatus);

        EventTypeEnum typeEnum =newStatus==OrderStatusEnum.PAID? EventTypeEnum.ORDER_PAID:EventTypeEnum.ORDER_COMPLETED;

        // 发布订单状态更新事件
        OrderOperationEvent event = new OrderOperationEvent(this, typeEnum, updatedOrder);
        eventPublisher.publishEvent(event);

        return true;
    }
}