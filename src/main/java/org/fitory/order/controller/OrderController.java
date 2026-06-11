package org.fitory.order.controller;

import lombok.RequiredArgsConstructor;
import org.fitory.auth.domain.User;
import org.fitory.common.dto.PageResponse;
import org.fitory.order.domain.Order;
import org.fitory.order.dto.OrderCreateRequest;
import org.fitory.order.dto.OrderItemResponse;
import org.fitory.order.dto.OrderResponse;
import org.fitory.order.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<PageResponse<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        int p = page != null ? page : 0;
        int s = size != null ? size : 10;

        PageResponse<OrderResponse> response = orderService.getMyOrders(user.getId(), p, s);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItemResponse>> getOrderItems(@PathVariable Long orderId) {
        List<OrderItemResponse> response = orderService.getOrderItems(orderId).stream()
                .map(OrderItemResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal User user,
            @RequestBody OrderCreateRequest request
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Order createdOrder = orderService.createOrder(user.getId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(createdOrder));
    }
}
