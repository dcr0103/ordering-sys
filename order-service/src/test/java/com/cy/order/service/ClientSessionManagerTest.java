package com.cy.order.service;

import com.cy.order.config.WebSocketProperties;
import com.cy.order.websocket.ClientSessionManager;
import jakarta.websocket.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientSessionManagerTest {

    @Mock
    private Session webSocketSession;
    
    @Mock
    private WebSocketProperties webSocketProperties;

    @InjectMocks
    private  ClientSessionManager clientSessionManager;

    @Test
    void testUserSessionManagement() {
        // Given
        String userId = "user1";
        when(webSocketSession.getId()).thenReturn("session1");
        when(webSocketSession.isOpen()).thenReturn(true);
        when(webSocketProperties.getHeartbeatTimeout()).thenReturn(30000L);

        // When - 添加用户会话
        clientSessionManager.addUserSession(userId, webSocketSession);

        // Then - 验证会话已添加
        assertNotNull(clientSessionManager.getUserSession(userId));
        assertEquals(1, clientSessionManager.getUserSessionCount());
        assertTrue(clientSessionManager.isUserOnline(userId));

        // When - 更新心跳
        clientSessionManager.updateUserHeartbeat(userId);

        // Then - 验证心跳已更新
        assertTrue(clientSessionManager.isUserOnline(userId));

        // When - 移除会话
        clientSessionManager.removeUserSession(userId);

        // Then - 验证会话已移除
        assertNull(clientSessionManager.getUserSession(userId));
        assertEquals(0, clientSessionManager.getUserSessionCount());
        assertFalse(clientSessionManager.isUserOnline(userId));
    }

    @Test
    void testStoreSessionManagement() {
        // Given
        String storeId = "store1";
        when(webSocketSession.getId()).thenReturn("session2");
        when(webSocketSession.isOpen()).thenReturn(true);
        when(webSocketProperties.getHeartbeatTimeout()).thenReturn(30000L);

        // When - 添加门店会话
        clientSessionManager.addStoreSession(storeId, webSocketSession);

        // Then - 验证会话已添加
        assertNotNull(clientSessionManager.getStoreSession(storeId));
        assertEquals(1, clientSessionManager.getStoreSessionCount());
        assertTrue(clientSessionManager.isStoreOnline(storeId));

        // When - 更新心跳
        clientSessionManager.updateStoreHeartbeat(storeId);

        // Then - 验证心跳已更新
        assertTrue(clientSessionManager.isStoreOnline(storeId));

        // When - 移除会话
        clientSessionManager.removeStoreSession(storeId);

        // Then - 验证会话已移除
        assertNull(clientSessionManager.getStoreSession(storeId));
        assertEquals(0, clientSessionManager.getStoreSessionCount());
        assertFalse(clientSessionManager.isStoreOnline(storeId));
    }
}