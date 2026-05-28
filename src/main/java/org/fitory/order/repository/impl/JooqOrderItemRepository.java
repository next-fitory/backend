package org.fitory.order.repository.impl;

import core.annotation.Repository;
import org.fitory.infra.DatabaseConfig;
import org.fitory.order.domain.OrderItem;
import org.fitory.order.repository.OrderItemRepository;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class JooqOrderItemRepository implements OrderItemRepository {

    private static final String TABLE = "order_items";
    private final DSLContext dsl;

    public JooqOrderItemRepository(DatabaseConfig databaseConfig) {
        this.dsl = databaseConfig.dsl();
    }

    @Override
    public OrderItem save(OrderItem orderItem) {
        Long id = dsl.insertInto(table(TABLE))
                .set(field("order_id"), orderItem.getOrderId())
                .set(field("product_id"), orderItem.getProductId())
                .set(field("quantity"), orderItem.getQuantity())
                .set(field("unit_price"), orderItem.getUnitPrice())
                .set(field("created_at"), orderItem.getCreatedAt())
                .returning(field("id", Long.class))
                .fetchOne(field("id", Long.class));

        return orderItem.toBuilder().id(id).build();
    }

    @Override
    public void saveAll(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return;
        }

        var insertQuery = dsl.insertInto(table(TABLE),
                field("order_id"), field("product_id"), field("quantity"), field("unit_price"), field("created_at"));

        for (OrderItem item : orderItems) {
            insertQuery.values(
                    item.getOrderId(),
                    item.getProductId(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getCreatedAt()
            );
        }

        insertQuery.execute();
    }

    @Override
    public List<OrderItem> findAllByOrderId(Long orderId) {
        return dsl.select(
                        field(TABLE + ".id"),
                        field(TABLE + ".order_id"),
                        field(TABLE + ".product_id"),
                        field(TABLE + ".quantity"),
                        field(TABLE + ".unit_price"),
                        field(TABLE + ".created_at"),
                        field("products.name").as("product_name"),
                        field("products.image_url")
                )
                .from(table(TABLE))
                .leftJoin(table("products")).on(field(TABLE + ".product_id").eq(field("products.id")))
                .where(field(TABLE + ".order_id").eq(orderId))
                .fetch()
                .map(this::toOrderItem);
    }

    private OrderItem toOrderItem(Record record) {
        return OrderItem.builder()
                .id(record.get("id", Long.class))
                .orderId(record.get("order_id", Long.class))
                .productId(record.get("product_id", Long.class))
                .productName(record.get("product_name", String.class))
                .imageUrl(record.get("image_url", String.class))
                .quantity(record.get("quantity", Integer.class))
                .unitPrice(record.get("unit_price", BigDecimal.class))
                .createdAt(record.get("created_at", LocalDateTime.class))
                .build();
    }
}