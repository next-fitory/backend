package org.fitory.order.dto;

import org.fitory.order.domain.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemResponse(
        Long id,
        Long orderId,
        Long productId,
        int quantity,
        BigDecimal unitPrice,
        LocalDateTime createdAt
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getOrderId(),
                item.getProductId(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getCreatedAt()
        );
    }
}