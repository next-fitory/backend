package org.fitory.cart.repository.impl;

import org.springframework.stereotype.Repository;
import org.fitory.cart.domain.CartItem;
import org.fitory.cart.dto.CartProductResponse;
import org.fitory.cart.repository.CartItemRepository;
import org.fitory.infra.DatabaseConfig;
import org.fitory.product.dto.ProductResponse;
import org.fitory.util.LocalDateTimeFormatter;
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
        Long id = dsl.insertInto(table(TABLE))
                .set(field("user_id"), cartItem.getUserId())
                .set(field("product_id"), cartItem.getProductId())
                .set(field("quantity"), cartItem.getQuantity())
                .set(field("created_at"), cartItem.getCreatedAt())
                .set(field("updated_at"), cartItem.getUpdatedAt())
                .returning(field("id", Long.class))
                .fetchOne(field("id", Long.class));
        return cartItem.toBuilder().id(id).build();
    }

    private CartItem update(CartItem cartItem) {
        dsl.update(table(TABLE))
                .set(field("quantity"), cartItem.getQuantity())
                .set(field("updated_at"), cartItem.getUpdatedAt())
                .where(field("id").eq(cartItem.getId()))
                .execute();
        return cartItem;
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
    public List<CartProductResponse> findCartProductsByUserId(Long userId, int page, int size) {
        return dsl.select(
                        field("p.id").as("p_id"),
                        field("p.name").as("name"),
                        field("p.price").as("price"),
                        field("p.discount_rate").as("discount_rate"),
                        field("p.stock").as("stock"),
                        field("p.image_url").as("image_url"),
                        field("b.name").as("brand_name"),
                        field("ci.quantity").as("quantity"),
                        field("ci.created_at").as("created_at"),
                        field("ci.updated_at").as("updated_at")
                )
                .from(table("cart_items").as("ci"))
                .join(table("products").as("p")).on(field("ci.product_id").eq(field("p.id")))
                .join(table("brands").as("b")).on(field("p.brand_id").eq(field("b.id")))
                .where(field("ci.user_id").eq(userId)
                        .and(field("p.deleted", Boolean.class).isFalse()))
                .orderBy(field("ci.created_at").desc())
                .limit(size)
                .offset((long) page * size)
                .fetch()
                .map(this::toCartProductResponse);
    }

    @Override
    public long countByUserId(Long userId) {
        return dsl.selectCount()
                .from(table("cart_items").as("ci"))
                .join(table("products").as("p")).on(field("ci.product_id").eq(field("p.id")))
                .where(field("ci.user_id").eq(userId)
                        .and(field("p.deleted", Boolean.class).isFalse()))
                .fetchOne(0, Long.class);
    }

    private CartProductResponse toCartProductResponse(Record r) {
        int price = r.get("price", Integer.class);
        int discountRate = r.get("discount_rate", Integer.class);
        int salePrice = (int) (price * (100 - discountRate) * 0.01);
        ProductResponse product = ProductResponse.builder()
                .id(r.get("p_id", Long.class))
                .name(r.get("name", String.class))
                .price(price)
                .salePrice(salePrice)
                .discountRate(discountRate)
                .stock(r.get("stock", Integer.class))
                .imageUrl(r.get("image_url", String.class))
                .brandName(r.get("brand_name", String.class))
                .build();
        return CartProductResponse.builder()
                .product(product)
                .quantity(r.get("quantity", Integer.class))
                .createdAt(LocalDateTimeFormatter.dateTime(r.get("created_at", LocalDateTime.class)))
                .updatedAt(LocalDateTimeFormatter.dateTime(r.get("updated_at", LocalDateTime.class)))
                .build();
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
