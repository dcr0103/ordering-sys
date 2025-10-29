package com.cy.order.controller;

import com.cy.order.dto.OrderDto;
import com.cy.order.dto.OrderItemDto;
import com.cy.order.dto.OrderRequestDto;
import com.cy.order.enums.OrderStatusEnum;
import com.cy.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void createOrder_WithValidRequest_ShouldReturnOrderId() throws Exception {
        // Given
        OrderRequestDto orderRequest = new OrderRequestDto();
        orderRequest.setUserId("user1");
        orderRequest.setStoreId("store1");
        orderRequest.setAmount(new BigDecimal("99.99"));
        
        OrderItemDto item = new OrderItemDto("product1", "Product 1", 2, new BigDecimal("49.99"));
        orderRequest.setItems(Arrays.asList(item));
        
        String expectedOrderId = "test-order-id";
        when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(expectedOrderId);

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"userId\": \"user1\",\n" +
                        "  \"storeId\": \"store1\",\n" +
                        "  \"amount\": 99.99,\n" +
                        "  \"items\": [\n" +
                        "    {\n" +
                        "      \"productId\": \"product1\",\n" +
                        "      \"productName\": \"Product 1\",\n" +
                        "      \"quantity\": 2,\n" +
                        "      \"price\": 49.99\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.orderId").value(expectedOrderId));
    }

    @Test
    void createOrder_WhenServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(orderService.createOrder(any(OrderRequestDto.class)))
                .thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "  \"userId\": \"user1\",\n" +
                        "  \"storeId\": \"store1\",\n" +
                        "  \"amount\": 99.99,\n" +
                        "  \"items\": []\n" +
                        "}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Internal server error"));
    }

    @Test
    void updateOrderStatus_WithValidParameters_ShouldReturnSuccess() throws Exception {
        // Given
        String orderId = "test-order-id";
        String userId = "user1";
        String storeId = "test-store-id";
        String status = "PAID";

        when(orderService.updateOrderStatus(eq(orderId), any(OrderStatusEnum.class), eq(storeId),eq(userId)))
                .thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/orders/status/{orderId}", orderId)
                .param("status", status)
                .param("storeId", storeId)
                .param("userId", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Order status updated successfully"));
    }

    @Test
    void updateOrderStatus_WithInvalidStatus_ShouldReturnBadRequest() throws Exception {
        // Given
        String orderId = "test-order-id";
        String status = "INVALID_STATUS";
        String storeId = "test-store-id";
        String userId = "user1";

        // When & Then
        mockMvc.perform(post("/api/orders/status/{orderId}", orderId)
                .param("status", status)
                .param("storeId", storeId)
                .param("userId", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid order status"));
    }

    @Test
    void updateOrderStatus_WhenServiceReturnsFalse_ShouldReturnBadRequest() throws Exception {
        // Given
        String orderId = "test-order-id";
        String status = "PAID";
        String storeId = "test-store-id";
        String userId = "user1";

        when(orderService.updateOrderStatus(eq(orderId), any(OrderStatusEnum.class), eq(storeId),eq(userId)))
                .thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/orders/status/{orderId}", orderId)
                .param("status", status)
                .param("storeId", storeId)
                .param("userId", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Failed to update order status, order may not exist or unauthorized"));
    }

    @Test
    void getOrderById_WithExistingOrder_ShouldReturnOrder() throws Exception {
        // Given
        String orderId = "test-order-id";
        OrderDto orderDto = OrderDto.builder()
                .id(orderId)
                .orderNumber("ORD123456")
                .userId("user1")
                .storeId("store1")
                .amount(new BigDecimal("100.00"))
                .status(OrderStatusEnum.CREATED)
                .items(Arrays.asList())
                .createTime(LocalDateTime.now())
                .build();

        when(orderService.getOrderById(orderId)).thenReturn(orderDto);

        // When & Then
        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.orderNumber").value("ORD123456"))
                .andExpect(jsonPath("$.userId").value("user1"))
                .andExpect(jsonPath("$.storeId").value("store1"));
    }

    @Test
    void getOrderById_WithNonExistingOrder_ShouldReturnNotFound() throws Exception {
        // Given
        String orderId = "non-existing-id";
        when(orderService.getOrderById(orderId)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Order not found"));
    }
}