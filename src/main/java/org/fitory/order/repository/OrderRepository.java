package org.fitory.order.repository;

import org.fitory.order.domain.Order;
import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
    List<Order> findAllByUserId(Long userId, int limit, int offset);
    long countByUserId(Long userId);
}