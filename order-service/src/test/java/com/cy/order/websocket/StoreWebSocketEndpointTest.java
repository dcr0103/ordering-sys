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
class StoreWebSocketEndpointTest {

    private StoreWebSocketEndpoint storeWebSocketEndpoint;
    
    @Mock
    private Session session;
    
    @Mock
    private ClientSessionManager clientSessionManager;
    
    @BeforeEach
    void setUp() {
        storeWebSocketEndpoint = new StoreWebSocketEndpoint();
    }
    
    @Test
    void testOnOpen() throws IOException {
        // Given
        when(session.getId()).thenReturn("testSessionId");
        
        // When
        storeWebSocketEndpoint.onOpen(session);
        
        // Then
        verify(session).getId();
    }
    
    @Test
    void testOnClose() {
        // Given
        when(session.getId()).thenReturn("testSessionId");
        
        try (MockedStatic<StoreWebSocketEndpoint> mocked = mockStatic(StoreWebSocketEndpoint.class)) {
            mocked.when(StoreWebSocketEndpoint::getClientSessionManager).thenReturn(clientSessionManager);
            
            // When
            storeWebSocketEndpoint.onClose(session);
            
            // Then
            verify(clientSessionManager).removeStoreSession("testSessionId");
        }
    }
    
    @Test
    void testOnError() {
        // Given
        Throwable throwable = new RuntimeException("Test exception");
        when(session.getId()).thenReturn("testSessionId");
        
        try (MockedStatic<StoreWebSocketEndpoint> mocked = mockStatic(StoreWebSocketEndpoint.class)) {
            mocked.when(StoreWebSocketEndpoint::getClientSessionManager).thenReturn(clientSessionManager);
            
            // When
            storeWebSocketEndpoint.onError(session, throwable);
            
            // Then
            verify(clientSessionManager).removeStoreSession("testSessionId");
        }
    }
    
    @Test
    void testOnMessageWithRegister() throws IOException {
        // Given
        String registerMessage = "{\"type\":\"connect\",\"clientId\":\"testStore123\"}";
        when(session.getId()).thenReturn("testSessionId");
        when(session.isOpen()).thenReturn(true);
        
        try (MockedStatic<StoreWebSocketEndpoint> mocked = mockStatic(StoreWebSocketEndpoint.class)) {
            mocked.when(StoreWebSocketEndpoint::getClientSessionManager).thenReturn(clientSessionManager);
            
            // When
            storeWebSocketEndpoint.onMessage(registerMessage, session);
            
            // Then
            verify(clientSessionManager).addStoreSession("testStore123", session);
        }
    }
    
    @Test
    void testOnMessageWithHeartbeat() throws IOException {
        // Given
        String heartbeatMessage = "{\"type\":\"ping\",\"clientId\":\"testStore123\"}";
        when(session.getId()).thenReturn("testSessionId");
        when(session.isOpen()).thenReturn(true);
        
        try (MockedStatic<StoreWebSocketEndpoint> mocked = mockStatic(StoreWebSocketEndpoint.class)) {
            mocked.when(StoreWebSocketEndpoint::getClientSessionManager).thenReturn(clientSessionManager);
            
            // When
            storeWebSocketEndpoint.onMessage(heartbeatMessage, session);
            
            // Then
            verify(clientSessionManager).updateStoreHeartbeat("testStore123");
        }
    }
    
    @Test
    void testOnMessageWithUnknownType() throws IOException {
        // Given
        String unknownMessage = "{\"type\":\"unknown\"}";
        when(session.getId()).thenReturn("testSessionId");
        when(session.isOpen()).thenReturn(true);
        
        // When
        storeWebSocketEndpoint.onMessage(unknownMessage, session);
        
        // Then
        verify(session, atLeastOnce()).getId();
    }
    
    @Test
    void testSendMessageToStore() {
        try (MockedStatic<StoreWebSocketEndpoint> mocked = mockStatic(StoreWebSocketEndpoint.class)) {
            mocked.when(StoreWebSocketEndpoint::getClientSessionManager).thenReturn(clientSessionManager);
            when(clientSessionManager.getStoreSession("testStore123")).thenReturn(session);
            when(session.isOpen()).thenReturn(true);
            
            // When & Then
            assertDoesNotThrow(() -> StoreWebSocketEndpoint.sendMessageToStore("testStore123", "test message"));
        }
    }
    
    @Test
    void testGetStoreConnectedCount() {
        try (MockedStatic<StoreWebSocketEndpoint> mocked = mockStatic(StoreWebSocketEndpoint.class)) {
            mocked.when(StoreWebSocketEndpoint::getClientSessionManager).thenReturn(clientSessionManager);
            when(clientSessionManager.getStoreSessionCount()).thenReturn(3);
            
            // When
            int count = StoreWebSocketEndpoint.getConnectedStoreCount();
            
            // Then
            assertEquals(3, count);
        }
    }
}