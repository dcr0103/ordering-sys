package com.cy.order.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * APP端WebSocket端点
 * 处理与APP客户端的WebSocket连接
 */
@Slf4j
@Component
@ServerEndpoint("/ws/app")
public class AppWebSocketEndpoint {

    private static ClientSessionManager clientSessionManager;

    @Autowired
    public void setClientSessionManager(ClientSessionManager clientSessionManager) {
        AppWebSocketEndpoint.clientSessionManager = clientSessionManager;
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
        log.info("APP WebSocket连接建立成功: sessionId={}", session.getId());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        if (session == null){
            log.warn("APP WebSocket连接关闭: session is null");
            return;
        }
        String sessionId = session.getId();
        if (sessionId != null) {
            // 从用户会话中移除
            clientSessionManager.removeUserSession(sessionId);
            log.info("APP WebSocket连接关闭: sessionId={}", sessionId);
        } else {
            log.warn("APP WebSocket连接关闭: sessionId is null");
        }
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到APP WebSocket消息: sessionId={}, message={}", session.getId(), message);
        
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
                        clientSessionManager.addUserSession(clientId, session);
                        sendMessage(session, "{\"type\":\"connection_success\",\"message\":\"Connected successfully\", \"clientId\":\"" + clientId + "\"}");
                        log.info("APP用户连接成功: sessionId={}, clientId={}", sessionId, clientId);
                    } else {
                        log.warn("无法添加会话，sessionId为null");
                    }
                    break;
                case "ping":
                    sendMessage(session, "{\"type\":\"pong\"}");
                    // 更新最后活跃时间
                    String pingSessionId = session.getId();
                    if (pingSessionId != null) {
                        // 对于APP端，我们使用userId作为key存储会话
                        JsonNode userIdNode = jsonNode.get("clientId");
                        if (userIdNode != null) {
                            clientSessionManager.updateUserHeartbeat(userIdNode.asText());
                        }
                    }
                    break;
                default:
                    sendMessage(session, "{\"type\":\"error\",\"message\":\"Unknown message type: " + type + "\"}");
                    break;
            }
        } catch (Exception e) {
            log.error("处理APP WebSocket消息失败: sessionId={}", session.getId(), e);
            sendMessage(session, "{\"type\":\"error\",\"message\":\"Failed to process message\"}");
        }
    }

    /**
     * 发生错误时调用的方法
     */
    @OnError
    public void onError(Session session, Throwable error) {
        if (session == null){
            log.warn("APP WebSocket发生错误: session is null");
            return;
        }
        String sessionId = session.getId();
        log.error("APP WebSocket发生错误: sessionId={}", sessionId, error);
        if (sessionId != null) {
            clientSessionManager.removeUserSession(sessionId);
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
     * 向特定用户发送消息
     *
     * @param userId  用户ID
     * @param message 消息内容
     */
    public static void sendMessageToUser(String userId, String message) {
        // 使用ClientSessionManager获取用户会话并发送消息
        Session session = (Session) clientSessionManager.getUserSession(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("向用户发送消息失败: userId={}", userId, e);
            }
        }
    }

    /**
     * 获取当前连接的APP用户数
     *
     * @return 连接的用户数
     */
    public static int getConnectedAppUserCount() {
        return clientSessionManager.getUserSessionCount();
    }
}