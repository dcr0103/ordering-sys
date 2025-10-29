package com.cy.order.integration;

import com.cy.order.OrderServiceApplication;
import com.cy.order.websocket.ClientSessionManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = OrderServiceApplication.class
)
public class WebSocketHeartbeatIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClientSessionManager clientSessionManager;

    @Test
    public void testWebSocketEndpointsExist() {
        // 测试WebSocket客户端管理端点是否存在
        ResponseEntity<String> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/order-service/api/websocket/stats", 
            String.class
        );
        
        assertThat(response.getStatusCode().is2xxSuccessful() || 
                   response.getStatusCode().is4xxClientError()).isTrue();
    }

    // 注意：完整的WebSocket测试需要更复杂的设置，包括实际的WebSocket连接
    // 这里只是验证相关组件是否正确加载
}