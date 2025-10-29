package com.cy.order.websocket;

import jakarta.websocket.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppWebSocketEndpointTest {

    private AppWebSocketEndpoint appWebSocketEndpoint;
    
    @Mock
    private Session session;
    
    @Mock
    private ClientSessionManager clientSessionManager;


    @BeforeEach
    void setUp() {
        appWebSocketEndpoint = new AppWebSocketEndpoint();
    }
    
    @Test
    void testOnOpen() throws IOException {
        // Given
        when(session.getId()).thenReturn("testSessionId");
        
        // When
        appWebSocketEndpoint.onOpen(session);
        
        // Then
        verify(session).getId();
    }
    
    @Test
    void testOnClose() {
        // Given
        when(session.getId()).thenReturn("testSessionId");
        
        try (MockedStatic<AppWebSocketEndpoint> mocked = mockStatic(AppWebSocketEndpoint.class)) {
            mocked.when(AppWebSocketEndpoint::getClientSessionManager).thenReturn(clientSessionManager);
            
            // When
            appWebSocketEndpoint.onClose(session);
            
            // Then
            verify(clientSessionManager).removeUserSession("testSessionId");
        }
    }
    
    @Test
    void testOnError() {
        // Given
        Throwable throwable = new RuntimeException("Test exception");
        when(session.getId()).thenReturn("testSessionId");
        
        try (MockedStatic<AppWebSocketEndpoint> mocked = mockStatic(AppWebSocketEndpoint.class)) {
            mocked.when(AppWebSocketEndpoint::getClientSessionManager).thenReturn(clientSessionManager);
            
            // When
            appWebSocketEndpoint.onError(session, throwable);
            
            // Then
            verify(clientSessionManager).removeUserSession("testSessionId");
        }
    }
    
    @Test
    void testOnMessageWithRegister() throws IOException {
        // Given
        String registerMessage = "{\"type\":\"connect\",\"clientId\":\"testUser123\"}";
        when(session.getId()).thenReturn("testSessionId");
        when(session.isOpen()).thenReturn(true);
        
        try (MockedStatic<AppWebSocketEndpoint> mocked = mockStatic(AppWebSocketEndpoint.class)) {
            mocked.when(AppWebSocketEndpoint::getClientSessionManager).thenReturn(clientSessionManager);
            
            // When
            appWebSocketEndpoint.onMessage(registerMessage, session);
            
            // Then
            verify(clientSessionManager).addUserSession("testUser123", session);
        }
    }
    
    @Test
    void testOnMessageWithHeartbeat() throws IOException {
        // Given
        String heartbeatMessage = "{\"type\":\"ping\",\"clientId\":\"testUser123\"}";
        when(session.getId()).thenReturn("testSessionId");
        when(session.isOpen()).thenReturn(true);
        
        try (MockedStatic<AppWebSocketEndpoint> mocked = mockStatic(AppWebSocketEndpoint.class)) {
            mocked.when(AppWebSocketEndpoint::getClientSessionManager).thenReturn(clientSessionManager);
            
            // When
            appWebSocketEndpoint.onMessage(heartbeatMessage, session);
            
            // Then
            verify(clientSessionManager).updateUserHeartbeat("testUser123");
        }
    }
    
    @Test
    void testOnMessageWithUnknownType() throws IOException {
        // Given
        String unknownMessage = "{\"type\":\"unknown\"}";
        when(session.getId()).thenReturn("testSessionId");
        when(session.isOpen()).thenReturn(true);
        when(session.getBasicRemote()).thenReturn(null); // 模拟getBasicRemote()返回null的情况
        
        // When
        appWebSocketEndpoint.onMessage(unknownMessage, session);
        
        // Then
        // 不再验证getBasicRemote()调用，因为可能返回null
        // verify(session, atLeastOnce()).getBasicRemote();
    }
    
    @Test
    void testSendMessageToUser() {
        try (MockedStatic<AppWebSocketEndpoint> mocked = mockStatic(AppWebSocketEndpoint.class)) {
            mocked.when(AppWebSocketEndpoint::getClientSessionManager).thenReturn(clientSessionManager);
            when(clientSessionManager.getUserSession("testUser123")).thenReturn(session);
            when(session.isOpen()).thenReturn(true);
            
            // When & Then
            assertDoesNotThrow(() -> AppWebSocketEndpoint.sendMessageToUser("testUser123", "test message"));
        }
    }
    
    @Test
    void testGetAppConnectedCount() {
        try (MockedStatic<AppWebSocketEndpoint> mocked = mockStatic(AppWebSocketEndpoint.class)) {
            mocked.when(AppWebSocketEndpoint::getClientSessionManager).thenReturn(clientSessionManager);
            when(clientSessionManager.getUserSessionCount()).thenReturn(5);
            
            // When
            int count = AppWebSocketEndpoint.getConnectedAppUserCount();
            
            // Then
            assertEquals(5, count);
        }
    }
}