package com.cy.order.controller;

import com.cy.order.websocket.ClientSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * WebSocket客户端管理控制器
 * 提供WebSocket客户端连接管理和监控功能
 */
@RestController
@RequestMapping("/api/websocket")
@RequiredArgsConstructor
@Slf4j
public class WebSocketClientController {

    private final ClientSessionManager clientSessionManager;

    /**
     * 获取在线用户列表
     * 
     * @return 在线用户ID列表
     */
    @GetMapping("/users/online")
    public ResponseEntity<Set<String>> getOnlineUsers() {
        Set<String> onlineUsers = clientSessionManager.getAllUserIds();
        log.info("获取在线用户列表，共{}个用户在线", onlineUsers.size());
        return ResponseEntity.ok(onlineUsers);
    }

    /**
     * 获取在线门店列表
     * 
     * @return 在线门店ID列表
     */
    @GetMapping("/stores/online")
    public ResponseEntity<Set<String>> getOnlineStores() {
        Set<String> onlineStores = clientSessionManager.getAllStoreIds();
        log.info("获取在线门店列表，共{}个门店在线", onlineStores.size());
        return ResponseEntity.ok(onlineStores);
    }

    /**
     * 获取在线统计信息
     * 
     * @return 在线统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<WebSocketStats> getWebSocketStats() {
        WebSocketStats stats = WebSocketStats.builder()
                .onlineUserCount(clientSessionManager.getUserSessionCount())
                .onlineStoreCount(clientSessionManager.getStoreSessionCount())
                .build();
        log.info("获取WebSocket统计信息: {}", stats);
        return ResponseEntity.ok(stats);
    }

    /**
     * 测试WebSocket控制器是否正常工作
     * 
     * @param message 测试消息
     * @return 响应结果
     */
    @PostMapping("/test/message")
    public ResponseEntity<String> testWebSocketMessage(@RequestBody String message) {
        log.info("测试WebSocket消息处理: {}", message);
        return ResponseEntity.ok("消息已接收: " + message);
    }

    /**
     * WebSocket统计信息实体
     */
    @lombok.Data
    @lombok.Builder
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class WebSocketStats {
        private int onlineUserCount;
        private int onlineStoreCount;
    }
}