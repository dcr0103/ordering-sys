package com.cy.order.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 门店端WebSocket端点
 * 处理与门店客户端的WebSocket连接
 */
@Slf4j
@Component
@ServerEndpoint("/ws/store")
public class StoreWebSocketEndpoint {

    private static ClientSessionManager clientSessionManager;
    
    @Autowired
    public void setClientSessionManager(ClientSessionManager clientSessionManager) {
        StoreWebSocketEndpoint.clientSessionManager = clientSessionManager;
    }
    
    public static ClientSessionManager getClientSessionManager() {
        return clientSessionManager;
    }
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        log.info("门店 WebSocket连接建立成功: sessionId={}", session.getId());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        String sessionId = session.getId();
        if (clientSessionManager!=null&&sessionId != null) {
            // 从门店会话中移除
            clientSessionManager.removeStoreSession(sessionId);
            log.info("门店 WebSocket连接关闭: sessionId={}", sessionId);
        } else {
            log.warn("门店 WebSocket连接关闭: sessionId is null");
        }
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到门店 WebSocket消息: sessionId={}, message={}", session.getId(), message);
        
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            JsonNode typeNode = jsonNode.get("type");
            
            if (typeNode == null) {
                sendMessage(session, "{\"type\":\"error\",\"message\":\"Missing 'type' field in message\"}");
                return;
            }
            
            String type = typeNode.asText();
            
            switch (type) {
                case "connect":
                    JsonNode clientIdNode = jsonNode.get("clientId");
                    if (clientIdNode == null) {
                        sendMessage(session, "{\"type\":\"error\",\"message\":\"Missing 'clientId' field in connect message\"}");
                        return;
                    }
                    
                    String clientId = clientIdNode.asText();
                    String sessionId = session.getId();
                    if (sessionId != null) {
                        clientSessionManager.addStoreSession(clientId, session);
                        sendMessage(session, "{\"type\":\"connection_success\",\"message\":\"Connected successfully\", \"clientId\":\"" + clientId + "\"}");
                        log.info("门店连接成功: sessionId={}, clientId={}", sessionId, clientId);
                    } else {
                        log.warn("无法添加会话，sessionId为null");
                    }
                    break;
                case "ping":
                    sendMessage(session, "{\"type\":\"pong\"}");
                    // 更新最后活跃时间
                    String pingSessionId = session.getId();
                    if (pingSessionId != null) {
                        // 对于门店端，我们使用storeId作为key存储会话
                        JsonNode storeIdNode = jsonNode.get("clientId");
                        if (storeIdNode != null) {
                            clientSessionManager.updateStoreHeartbeat(storeIdNode.asText());
                        }
                    }
                    break;
                default:
                    sendMessage(session, "{\"type\":\"error\",\"message\":\"Unknown message type: " + type + "\"}");
                    break;
            }
        } catch (Exception e) {
            log.error("处理门店 WebSocket消息失败: sessionId={}", session.getId(), e);
            sendMessage(session, "{\"type\":\"error\",\"message\":\"Failed to process message\"}");
        }
    }

    /**
     * 发生错误时调用的方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        String sessionId = session.getId();
        log.error("门店 WebSocket发生错误: sessionId={}", sessionId, error);
        if (clientSessionManager!=null&&sessionId != null) {
            clientSessionManager.removeStoreSession(sessionId);
        }
    }

    /**
     * 向特定会话发送消息
     *
     * @param session 目标会话
     * @param message 消息内容
     */
    private void sendMessage(Session session, String message) {
        try {
            if (session.isOpen()) {
                // 检查getBasicRemote()是否为null
                if (session.getBasicRemote() != null) {
                    session.getBasicRemote().sendText(message);
                } else {
                    log.warn("无法发送消息，BasicRemote为null: sessionId={}", session.getId());
                }
            }
        } catch (IOException e) {
            log.error("发送消息失败: sessionId={}", session.getId(), e);
        }
    }

    /**
     * 向特定门店发送消息
     *
     * @param storeId 门店ID
     * @param message 消息内容
     */
    public static void sendMessageToStore(String storeId, String message) {
        // 使用ClientSessionManager获取门店会话并发送消息
        Session session = (Session) clientSessionManager.getStoreSession(storeId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("向门店发送消息失败: storeId={}", storeId, e);
            }
        }
    }

    /**
     * 获取当前连接的门店数
     *
     * @return 连接的门店数
     */
    public static int getConnectedStoreCount() {
        return clientSessionManager.getStoreSessionCount();
    }
}