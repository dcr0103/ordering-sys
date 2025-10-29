package com.cy.order.enums;

/**
 * 订单状态枚举
 */
public enum OrderStatusEnum {
    CREATED,      // 已创建
    PAID,         // 已支付
    CANCELLED,    // 已取消
    PROCESSING,   // 处理中
    COMPLETED,    // 已完成
    REFUNDED      // 已退款
}