package org.fitory.order.controller;

import core.annotation.RestController;
import lombok.RequiredArgsConstructor;
import mvc.ResponseEntity;
import mvc.annotation.*;
import org.fitory.auth.domain.User;
import org.fitory.order.domain.Order;
import org.fitory.order.dto.OrderCreateRequest;
import org.fitory.order.dto.OrderItemResponse;
import org.fitory.order.dto.OrderResponse;
import org.fitory.order.service.OrderService;
import security.annotation.CurrentUser;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // GET /api/orders (내 주문 목록 조회)
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(@CurrentUser User user) {
        if (user == null) {
            return ResponseEntity.unauthorized(null);
        }
        List<OrderResponse> response = orderService.getMyOrders(user.getId()).stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // GET /api/orders/{orderId}/items (주문 상세 아이템 조회)
    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemResponse>> getOrderItems(@PathVariable Long orderId) {
        List<OrderItemResponse> response = orderService.getOrderItems(orderId).stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // POST /api/orders (주문 생성)
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @CurrentUser User user,
            @RequestBody OrderCreateRequest request
    ) {
        if (user == null) {
            return ResponseEntity.unauthorized(null);
        }
        Order createdOrder = orderService.createOrder(user.getId(), request);

        return ResponseEntity.created(OrderResponse.from(createdOrder));
    }
}