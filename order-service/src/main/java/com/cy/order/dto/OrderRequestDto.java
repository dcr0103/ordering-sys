package com.cy.order.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 订单请求类
 */
@Data
public class OrderRequestDto {
    private String userId;
    private String storeId;
    private BigDecimal amount;
    private List<OrderItemDto> items;
}