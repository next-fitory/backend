package org.fitory.order.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private int quantity;
    private BigDecimal unitPrice;
    private LocalDateTime createdAt;
}