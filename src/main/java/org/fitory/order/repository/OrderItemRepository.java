package org.fitory.order.repository;

import org.fitory.order.domain.OrderItem;
import java.util.List;

public interface OrderItemRepository {
    OrderItem save(OrderItem orderItem);
    void saveAll(List<OrderItem> orderItems);
    List<OrderItem> findAllByOrderId(Long orderId);
}