package org.fitory.order.repository.impl;

import core.annotation.Repository;
import org.fitory.infra.DatabaseConfig;
import org.fitory.order.domain.Order;
import org.fitory.order.domain.OrderStatus;
import org.fitory.order.repository.OrderRepository;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class JooqOrderRepository implements OrderRepository {

    private static final String TABLE = "orders";
    private final DSLContext dsl;

    public JooqOrderRepository(DatabaseConfig databaseConfig) {
        this.dsl = databaseConfig.dsl();
    }

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            return insert(order);
        }
        return update(order);
    }

    private Order insert(Order order) {
        Long id = dsl.insertInto(table(TABLE))
                .set(field("user_id"), order.getUserId())
                .set(field("total_price"), order.getTotalPrice())
                .set(field("status"), order.getStatus().name().toLowerCase())
                .set(field("created_at"), order.getCreatedAt())
                .returning(field("id", Long.class))
                .fetchOne(field("id", Long.class));

        return order.toBuilder().id(id).build();
    }

    private Order update(Order order) {
        dsl.update(table(TABLE))
                .set(field("status"), order.getStatus().name().toLowerCase())
                .where(field("id").eq(order.getId()))
                .execute();
        return order;
    }

    @Override
    public Optional<Order> findById(Long id) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("id").eq(id))
                .fetchOptional()
                .map(this::toOrder);
    }

    @Override
    public List<Order> findAllByUserId(Long userId, int limit, int offset) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("user_id").eq(userId))
                .orderBy(field("created_at").desc())
                .limit(limit)
                .offset(offset)
                .fetch()
                .map(this::toOrder);
    }

    @Override
    public long countByUserId(Long userId) {
        Long count = dsl.selectCount()
                .from(table(TABLE))
                .where(field("user_id").eq(userId))
                .fetchOne(0, Long.class);
        return count != null ? count : 0L;
    }

    private Order toOrder(Record record) {
        return Order.builder()
                .id(record.get("id", Long.class))
                .userId(record.get("user_id", Long.class))
                .totalPrice(record.get("total_price", BigDecimal.class))
                .status(OrderStatus.valueOf(record.get("status", String.class).toUpperCase()))
                .createdAt(record.get("created_at", LocalDateTime.class))
                .build();
    }
}