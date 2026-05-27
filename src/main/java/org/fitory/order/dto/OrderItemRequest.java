package org.fitory.order.dto;

import java.math.BigDecimal;

public record OrderItemRequest(
        Long productId,
        int quantity,
        BigDecimal unitPrice
) {}