package org.fitory.order.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.auth.domain.User;
import org.fitory.order.domain.Order;
import org.fitory.order.domain.OrderItem;
import org.fitory.order.dto.OrderCreateRequest;
import org.fitory.order.service.OrderService;
import security.annotation.CurrentUser;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // GET /api/orders (내 주문 목록 조회)
    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders(@CurrentUser User user) {
        if (user == null) {
            return ResponseEntity.unauthorized(null);
        }
        return ResponseEntity.ok(orderService.getMyOrders(user.getId()));
    }

    // GET /api/orders/{orderId}/items (주문 상세 아이템 조회)
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderItems(orderId));
    }

    // POST /api/orders (주문 생성)
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @CurrentUser User user,
            @RequestBody OrderCreateRequest request
    ) {
        if (user == null) {
            return ResponseEntity.unauthorized(null);
        }
        Order createdOrder = orderService.createOrder(user.getId(), request);
        return ResponseEntity.created(createdOrder);
    }
}