package com.cy.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * WebSocket配置属性类
 * 用于配置WebSocket相关参数
 */
@Data
@Component
@ConfigurationProperties(prefix = "websocket")
public class WebSocketProperties {
    
    /**
     * 心跳超时时间（毫秒）
     * 默认300秒
     */
    private long heartbeatTimeout = 300000;
    
    /**
     * 清理任务执行间隔（毫秒）
     * 默认60秒
     */
    private long cleanupInterval = 60000;
}