package org.fitory.order.service;

import org.fitory.order.domain.Order;
import org.fitory.order.domain.OrderItem;
import org.fitory.order.dto.OrderCreateRequest;

import java.util.List;

public interface OrderService {
    List<Order> getMyOrders(Long userId);
    List<OrderItem> getOrderItems(Long orderId);
    Order createOrder(Long userId, OrderCreateRequest request);
}