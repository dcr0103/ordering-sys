package com.cy.order.websocket;

import com.cy.order.config.WebSocketProperties;
import jakarta.websocket.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 客户端会话管理器
 * 用于缓存和管理WebSocket客户端连接
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClientSessionManager {

    private final WebSocketProperties webSocketProperties;

    // 存储用户会话 key: clientId, value: SessionInfo
    private final Map<String, SessionInfo> userSessions = new ConcurrentHashMap<>();
    
    // 存储门店会话 key: clientId, value: SessionInfo
    private final Map<String, SessionInfo> storeSessions = new ConcurrentHashMap<>();

    /**
     * 会话信息内部类
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public class SessionInfo {
        private Session session;
        private LocalDateTime lastHeartbeat;
        private String sessionId;
        
        public boolean isExpired() {
            long timeout = webSocketProperties.getHeartbeatTimeout() / 1000;
            return lastHeartbeat != null && 
                   LocalDateTime.now().isAfter(lastHeartbeat.plusSeconds(timeout));
        }
    }

    /**
     * 添加用户会话
     * 
     * @param userId 用户ID
     * @param session WebSocket会话
     */
    public void addUserSession(String userId, Session session) {
        SessionInfo sessionInfo = new SessionInfo(session, LocalDateTime.now(), session.getId());
        userSessions.put(userId, sessionInfo);
        log.info("用户会话已添加: userId={}, sessionId={}, remoteAddress={}", userId, session.getId(), session.getRequestURI());
    }

    /**
     * 添加门店会话
     * 
     * @param storeId 门店ID
     * @param session WebSocket会话
     */
    public void addStoreSession(String storeId, Session session) {
        SessionInfo sessionInfo = new SessionInfo(session, LocalDateTime.now(), session.getId());
        storeSessions.put(storeId, sessionInfo);
        log.info("门店会话已添加: storeId={}, sessionId={}, remoteAddress={}", storeId, session.getId(), session.getRequestURI());
    }

    /**
     * 根据用户ID获取会话
     * 
     * @param userId 用户ID
     * @return WebSocket会话
     */
    public Session getUserSession(String userId) {
        SessionInfo sessionInfo = userSessions.get(userId);
        if (sessionInfo != null) {
            log.debug("获取用户会话: userId={}, sessionId={}", userId, sessionInfo.getSessionId());
        } else {
            log.debug("未找到用户会话: userId={}", userId);
        }
        return sessionInfo != null ? sessionInfo.getSession() : null;
    }

    /**
     * 根据门店ID获取会话
     * 
     * @param storeId 门店ID
     * @return WebSocket会话
     */
    public Session getStoreSession(String storeId) {
        SessionInfo sessionInfo = storeSessions.get(storeId);
        if (sessionInfo != null) {
            log.debug("获取门店会话: storeId={}, sessionId={}", storeId, sessionInfo.getSessionId());
        } else {
            log.debug("未找到门店会话: storeId={}", storeId);
        }
        return sessionInfo != null ? sessionInfo.getSession() : null;
    }

    /**
     * 获取所有用户ID
     * 
     * @return 用户ID集合
     */
    public Set<String> getAllUserIds() {
        return userSessions.keySet();
    }

    /**
     * 获取所有门店ID
     * 
     * @return 门店ID集合
     */
    public Set<String> getAllStoreIds() {
        return storeSessions.keySet();
    }

    /**
     * 获取用户会话数量
     * 
     * @return 用户会话数量
     */
    public int getUserSessionCount() {
        return userSessions.size();
    }

    /**
     * 获取门店会话数量
     * 
     * @return 门店会话数量
     */
    public int getStoreSessionCount() {
        return storeSessions.size();
    }

    /**
     * 检查用户是否在线（会话存在且未过期）
     * 
     * @param userId 用户ID
     * @return 是否在线
     */
    public boolean isUserOnline(String userId) {
        SessionInfo sessionInfo = userSessions.get(userId);
        if (sessionInfo == null) {
            log.debug("用户不在线（未找到会话）: userId={}", userId);
            return false;
        }
        
        Session session = sessionInfo.getSession();
        if (session == null || !session.isOpen()) {
            log.debug("用户不在线（会话已关闭）: userId={}", userId);
            return false;
        }
        
        boolean online = !sessionInfo.isExpired();
        log.debug("用户在线状态: userId={}, isOnline={}", userId, online);
        return online;
    }

    /**
     * 检查门店是否在线（会话存在且未过期）
     * 
     * @param storeId 门店ID
     * @return 是否在线
     */
    public boolean isStoreOnline(String storeId) {
        SessionInfo sessionInfo = storeSessions.get(storeId);
        if (sessionInfo == null) {
            log.debug("门店不在线（未找到会话）: storeId={}", storeId);
            return false;
        }
        
        Session session = sessionInfo.getSession();
        if (session == null || !session.isOpen()) {
            log.debug("门店不在线（会话已关闭）: storeId={}", storeId);
            return false;
        }
        
        boolean online = !sessionInfo.isExpired();
        log.debug("门店在线状态: storeId={}, isOnline={}", storeId, online);
        return online;
    }

    /**
     * 获取用户的最后心跳时间
     * 
     * @param userId 用户ID
     * @return 最后心跳时间
     */
    public LocalDateTime getUserLastHeartbeat(String userId) {
        SessionInfo sessionInfo = userSessions.get(userId);
        LocalDateTime lastHeartbeat = sessionInfo != null ? sessionInfo.getLastHeartbeat() : null;
        log.debug("获取用户最后心跳时间: userId={}, lastHeartbeat={}", userId, lastHeartbeat);
        return lastHeartbeat;
    }

    /**
     * 获取门店的最后心跳时间
     * 
     * @param storeId 门店ID
     * @return 最后心跳时间
     */
    public LocalDateTime getStoreLastHeartbeat(String storeId) {
        SessionInfo sessionInfo = storeSessions.get(storeId);
        LocalDateTime lastHeartbeat = sessionInfo != null ? sessionInfo.getLastHeartbeat() : null;
        log.debug("获取门店最后心跳时间: storeId={}, lastHeartbeat={}", storeId, lastHeartbeat);
        return lastHeartbeat;
    }

    /**
     * 更新用户心跳时间
     * 
     * @param userId 用户ID
     */
    public void updateUserHeartbeat(String userId) {
        SessionInfo sessionInfo = userSessions.get(userId);
        if (sessionInfo != null) {
            sessionInfo.setLastHeartbeat(LocalDateTime.now());
            log.debug("用户心跳更新: userId={}", userId);
        } else {
            log.warn("尝试更新不存在的用户心跳: userId={}", userId);
        }
    }

    /**
     * 更新门店心跳时间
     * 
     * @param storeId 门店ID
     */
    public void updateStoreHeartbeat(String storeId) {
        SessionInfo sessionInfo = storeSessions.get(storeId);
        if (sessionInfo != null) {
            sessionInfo.setLastHeartbeat(LocalDateTime.now());
            log.debug("门店心跳更新: storeId={}", storeId);
        } else {
            log.warn("尝试更新不存在的门店心跳: storeId={}", storeId);
        }
    }

    /**
     * 移除用户会话
     * 
     * @param userId 用户ID
     */
    public void removeUserSession(String userId) {
        SessionInfo removed = userSessions.remove(userId);
        if (removed != null) {
            log.info("用户会话已移除: userId={}, sessionId={}", userId, removed.getSessionId());
            closeSession(removed.getSession());
        } else {
            log.warn("尝试移除不存在的用户会话: userId={}", userId);
        }
    }

    /**
     * 移除门店会话
     * 
     * @param storeId 门店ID
     */
    public void removeStoreSession(String storeId) {
        SessionInfo removed = storeSessions.remove(storeId);
        if (removed != null) {
            log.info("门店会话已移除: storeId={}, sessionId={}", storeId, removed.getSessionId());
            closeSession(removed.getSession());
        } else {
            log.warn("尝试移除不存在的门店会话: storeId={}", storeId);
        }
    }

    /**
     * 安全关闭会话
     * 
     * @param session WebSocket会话
     */
    private void closeSession(Session session) {
        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log.warn("关闭WebSocket会话时发生错误: sessionId={}", session.getId(), e);
            }
        }
    }

    /**
     * 定期清理无效会话和过期会话
     * 使用配置的清理间隔时间
     */
    @Scheduled(fixedRateString = "${websocket.cleanup-interval:30000}")
    public void cleanInvalidSessions() {
        log.debug("开始清理无效会话");

        // 清理无效或过期的用户会话
        userSessions.entrySet().removeIf(entry -> {
            SessionInfo sessionInfo = entry.getValue();
            Session session = sessionInfo.getSession();
            
            if (session == null || !session.isOpen() || sessionInfo.isExpired()) {
                log.info("清理无效或过期用户会话: userId={}, sessionId={}", entry.getKey(), 
                         sessionInfo != null ? sessionInfo.getSessionId() : "null");
                return true;
            }
            return false;
        });

        // 清理无效或过期的门店会话
        storeSessions.entrySet().removeIf(entry -> {
            SessionInfo sessionInfo = entry.getValue();
            Session session = sessionInfo.getSession();
            
            if (session == null || !session.isOpen() || sessionInfo.isExpired()) {
                log.info("清理无效或过期门店会话: storeId={}, sessionId={}", entry.getKey(), 
                         sessionInfo != null ? sessionInfo.getSessionId() : "null");

                closeSession(session);
                return true;
            }
            return false;
        });

        log.debug("无效会话清理完成，当前用户会话数: {}, 门店会话数: {}", 
                  userSessions.size(), storeSessions.size());
    }
}