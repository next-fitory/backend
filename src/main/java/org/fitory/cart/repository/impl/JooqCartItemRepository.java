package org.fitory.cart.repository.impl;

import core.annotation.Repository;
import org.fitory.cart.domain.CartItem;
import org.fitory.cart.repository.CartItemRepository;
import org.fitory.infra.DatabaseConfig;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class JooqCartItemRepository implements CartItemRepository {
    private static final String TABLE = "cart_items";
    private final DSLContext dsl;

    public JooqCartItemRepository(DatabaseConfig databaseConfig) {
        this.dsl = databaseConfig.dsl();
    }

    @Override
    public CartItem save(CartItem cartItem) {
        if (cartItem.getId() == null) {
            return insert(cartItem);
        }
        return update(cartItem);
    }

    private CartItem insert(CartItem cartItem) {
        Record record = dsl.insertInto(table(TABLE))
                .set(field("user_id"), cartItem.getUserId())
                .set(field("product_id"), cartItem.getProductId())
                .set(field("quantity"), cartItem.getQuantity())
                .set(field("created_at"), cartItem.getCreatedAt())
                .set(field("updated_at"), cartItem.getUpdatedAt())
                .returning()
                .fetchOne();
        return toCartItem(record);
    }

    private CartItem update(CartItem cartItem) {
        Record record = dsl.update(table(TABLE))
                .set(field("quantity"), cartItem.getQuantity())
                .set(field("updated_at"), cartItem.getUpdatedAt())
                .where(field("id").eq(cartItem.getId()))
                .returning()
                .fetchOne();
        return toCartItem(record);
    }

    @Override
    public Optional<CartItem> findById(Long id) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("id").eq(id))
                .fetchOptional()
                .map(this::toCartItem);
    }

    @Override
    public Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("user_id").eq(userId).and(field("product_id").eq(productId)))
                .fetchOptional()
                .map(this::toCartItem);
    }

    @Override
    public List<CartItem> findAllByUserId(Long userId) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("user_id").eq(userId))
                .fetch()
                .map(this::toCartItem);
    }

    @Override
    public void deleteById(Long id) {
        dsl.deleteFrom(table(TABLE))
                .where(field("id").eq(id))
                .execute();
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        dsl.deleteFrom(table(TABLE))
                .where(field("user_id").eq(userId))
                .execute();
    }

    private CartItem toCartItem(Record record) {
        return CartItem.builder()
                .id(record.get("id", Long.class))
                .userId(record.get("user_id", Long.class))
                .productId(record.get("product_id", Long.class))
                .quantity(record.get("quantity", Integer.class))
                .createdAt(record.get("created_at", LocalDateTime.class))
                .updatedAt(record.get("updated_at", LocalDateTime.class))
                .build();
    }
}
