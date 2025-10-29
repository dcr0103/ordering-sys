package com.cy.order.controller;

import com.cy.order.service.OrderService;
import com.cy.order.dto.OrderRequestDto;
import com.cy.order.enums.OrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 * 提供订单相关的REST API接口
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单接口
     *
     * @param orderRequest 订单请求信息
     * @return 订单ID
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDto orderRequest) {
        try {
            String orderId = orderService.createOrder(orderRequest);
            log.info("创建订单成功: orderId={}", orderId);
            return ResponseEntity.ok().body("{\"success\": true, \"orderId\": \"" + orderId + "\"}");
        } catch (Exception e) {
            log.error("创建订单时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\": false, \"message\": \"Internal server error\"}");
        }
    }

    /**
     * 门店修改订单状态接口
     * 
     * @param orderId 订单ID
     * @param status 新的订单状态
     * @param userId 用户ID
     * @param storeId 门店ID
     * @return 响应结果
     */
    @PostMapping("/status/{orderId}")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String status,
            @RequestParam String userId,
            @RequestParam String storeId) {
        
        try {
            // 验证并转换状态参数
            OrderStatusEnum newStatus = OrderStatusEnum.valueOf(status.toUpperCase());
            
            // 调用服务层修改订单状态
            boolean success = orderService.updateOrderStatus(orderId, newStatus, storeId,userId);
            
            if (success) {
                log.info("门店修改订单状态成功: orderId={}, status={}, storeId={}", orderId, status, storeId);
                return ResponseEntity.ok().body("{\"success\": true, \"message\": \"Order status updated successfully\"}");
            } else {
                log.warn("门店修改订单状态失败: orderId={}, status={}, storeId={}", orderId, status, storeId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"success\": false, \"message\": \"Failed to update order status, order may not exist or unauthorized\"}");
            }
            
        } catch (IllegalArgumentException e) {
            log.error("无效的订单状态: {}", status);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"success\": false, \"message\": \"Invalid order status\"}");
        } catch (Exception e) {
            log.error("修改订单状态时发生错误: orderId={}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\": false, \"message\": \"Internal server error\"}");
        }
    }
    
    /**
     * 获取订单详情接口
     * 
     * @param orderId 订单ID
     * @return 订单信息
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable String orderId) {
        try {
            var order = orderService.getOrderById(orderId);
            if (order != null) {
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("{\"success\": false, \"message\": \"Order not found\"}");
            }
        } catch (Exception e) {
            log.error("查询订单详情时发生错误: orderId={}", orderId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"success\": false, \"message\": \"Internal server error\"}");
        }
    }
}