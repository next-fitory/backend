package org.fitory.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderCreateRequest(
        BigDecimal totalPrice,
        List<OrderItemRequest> items
) {}