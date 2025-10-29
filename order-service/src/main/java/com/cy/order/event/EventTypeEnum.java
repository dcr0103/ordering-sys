package com.cy.order.event;

/**
 * 事件类型枚举
 * 定义系统中所有支持的事件类型
 */
public enum EventTypeEnum {
    
    /**
     * 订单相关事件
     */
    ORDER_CREATED("order_created", "订单创建事件"),
    ORDER_CANCELLED("order_cancelled", "订单取消事件"),
    ORDER_UPDATED("order_updated", "订单更新事件"),
    ORDER_PAID("order_paid", "订单支付事件"),
    ORDER_SHIPPED("order_shipped", "订单发货事件"),
    ORDER_COMPLETED("order_completed", "订单完成事件"),
    ORDER_REFUNDED("order_refunded", "订单退款事件"),
    
    /**
     * 支付相关事件
     */
    PAYMENT_SUCCESS("payment_success", "支付成功事件"),
    PAYMENT_FAILED("payment_failed", "支付失败事件"),
    
    /**
     * 库存相关事件
     */
    INVENTORY_UPDATED("inventory_updated", "库存更新事件"),
    INVENTORY_LOW("inventory_low", "库存不足事件"),
    
    /**
     * 用户相关事件
     */
    USER_REGISTERED("user_registered", "用户注册事件"),
    USER_LOGGED_IN("user_logged_in", "用户登录事件"),
    
    /**
     * 通知相关事件
     */
    NOTIFICATION_SENT("notification_sent", "通知发送事件"),
    NOTIFICATION_FAILED("notification_failed", "通知发送失败事件");

    /**
     * 事件类型代码
     */
    private final String code;

    /**
     * 事件类型描述
     */
    private final String description;

    /**
     * 构造函数
     * 
     * @param code 事件类型代码
     * @param description 事件类型描述
     */
    EventTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取事件类型代码
     * 
     * @return 事件类型代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取事件类型描述
     * 
     * @return 事件类型描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据代码获取事件类型枚举
     * 
     * @param code 事件类型代码
     * @return 事件类型枚举，如果未找到则返回null
     */
    public static EventTypeEnum fromCode(String code) {
        for (EventTypeEnum eventType : EventTypeEnum.values()) {
            if (eventType.getCode().equals(code)) {
                return eventType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "EventTypeEnum{" +
                "code='" + code + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}