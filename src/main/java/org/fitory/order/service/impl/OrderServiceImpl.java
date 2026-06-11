package org.fitory.order.service.impl;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.fitory.common.dto.PageResponse;
import org.fitory.order.domain.Order;
import org.fitory.order.domain.OrderItem;
import org.fitory.order.domain.OrderStatus;
import org.fitory.order.dto.OrderCreateRequest;
import org.fitory.order.dto.OrderResponse;
import org.fitory.order.repository.OrderItemRepository;
import org.fitory.order.repository.OrderRepository;
import org.fitory.order.service.OrderService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public PageResponse<OrderResponse> getMyOrders(Long userId, int page, int size) {
        int offset = page * size;

        List<Order> orders = orderRepository.findAllByUserId(userId, size, offset);
        long totalElements = orderRepository.countByUserId(userId);

        List<OrderResponse> content = orders.stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());

        return PageResponse.of(content, page, size, totalElements);
    }

    @Override
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findAllByOrderId(orderId);
    }

    @Override
    public Order createOrder(Long userId, OrderCreateRequest request) {
        // Order 생성 및 저장
        Order newOrder = Order.builder()
                .userId(userId)
                .totalPrice(request.totalPrice())
                .status(OrderStatus.PAID) // 일단 결제 완료로 세팅
                .createdAt(LocalDateTime.now())
                .build();

        Order savedOrder = orderRepository.save(newOrder);

        // 전달받은 DTO 리스트를 OrderItem 엔티티로 변환
        List<OrderItem> orderItems = request.items().stream()
                .map(itemDto -> OrderItem.builder()
                        .orderId(savedOrder.getId()) // 방금 저장된 주문서의 ID 연결
                        .productId(itemDto.productId())
                        .quantity(itemDto.quantity())
                        .unitPrice(itemDto.unitPrice())
                        .createdAt(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        // 주문 상품 내역 한 번에 묶어서 DB에 저장
        orderItemRepository.saveAll(orderItems);

        return savedOrder;
    }
}