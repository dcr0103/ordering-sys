package com.cy.order.dto;

import com.cy.order.enums.OrderStatusEnum;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private String id;
    private String orderNumber;
    private String userId;
    private String storeId;
    private BigDecimal amount;
    private OrderStatusEnum status;
    private List<OrderItemDto> items;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}