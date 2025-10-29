package com.cy.order.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * 订单项
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto {
    private String productId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}