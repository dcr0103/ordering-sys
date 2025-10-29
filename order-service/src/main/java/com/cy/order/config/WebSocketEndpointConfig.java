package com.cy.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket端点配置类
 * 用于启用标准的WebSocket端点支持
 */
@Configuration
public class WebSocketEndpointConfig {

    /**
     * 配置ServerEndpointExporter Bean
     * 该Bean会自动注册使用@ServerEndpoint注解的WebSocket端点
     *
     * @return ServerEndpointExporter实例
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}