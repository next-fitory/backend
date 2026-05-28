package org.fitory.order.service;

import org.fitory.common.dto.PageResponse;
import org.fitory.order.domain.Order;
import org.fitory.order.domain.OrderItem;
import org.fitory.order.dto.OrderCreateRequest;
import org.fitory.order.dto.OrderResponse;

import java.util.List;

public interface OrderService {
    PageResponse<OrderResponse> getMyOrders(Long userId, int page, int size);
    List<OrderItem> getOrderItems(Long orderId);
    Order createOrder(Long userId, OrderCreateRequest request);
}