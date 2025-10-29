package com.cy.order.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.boot.web.server.WebServer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupListener implements ApplicationListener<ContextRefreshedEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartupListener.class);
    
    @Autowired
    private WebServerApplicationContext webServerApplicationContext;
    

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        WebServer webServer = webServerApplicationContext.getWebServer();
        int port = webServer.getPort();
        
        String contextPath = "/order-service";
        String apiPath = "/api/orders";
        String appWsPath = "/ws/app";
        String storeWsPath = "/ws/store";
        
        String baseUrl = "http://localhost:" + port + contextPath;
        String apiUrl = baseUrl + apiPath;
        String appWsUrl = "ws://localhost:" + port + contextPath + appWsPath;
        String storeWsUrl = "ws://localhost:" + port + contextPath + storeWsPath;
        
        logger.info("===============================================");
        logger.info("应用启动成功!");
        logger.info("API 地址: {}", apiUrl);
        logger.info("APP WebSocket 地址: {}", appWsUrl);
        logger.info("门店 WebSocket 地址: {}", storeWsUrl);
        logger.info("===============================================");
    }
}