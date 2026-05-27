package org.fitory.userlike.repository.impl;

import core.annotation.Repository;
import org.fitory.infra.DatabaseConfig;
import org.fitory.userlike.domain.UserLike;
import org.fitory.userlike.repository.UserLikeRepository;
import org.jooq.DSLContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class JooqUserLikeRepository implements UserLikeRepository {
    private static final String TABLE = "user_likes";
    private final DSLContext dsl;

    public JooqUserLikeRepository(DatabaseConfig databaseConfig) {
        this.dsl = databaseConfig.dsl();
    }

    @Override
    public UserLike save(UserLike userLike) {
        Long id = dsl.insertInto(table(TABLE))
                .set(field("user_id"), userLike.getUserId())
                .set(field("product_id"), userLike.getProductId())
                .set(field("created_at"), userLike.getCreatedAt())
                .returning(field("id", Long.class))
                .fetchOne(field("id", Long.class));
        return userLike.toBuilder().id(id).build();
    }

    @Override
    public Optional<UserLike> findById(Long id) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("id").eq(id))
                .fetchOptional()
                .map(this::toUserLike);
    }

    @Override
    public Optional<UserLike> findByUserIdAndProductId(Long userId, Long productId) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("user_id").eq(userId).and(field("product_id").eq(productId)))
                .fetchOptional()
                .map(this::toUserLike);
    }

    @Override
    public List<UserLike> findAllByUserId(Long userId) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("user_id").eq(userId))
                .orderBy(field("created_at").desc())
                .fetch()
                .map(this::toUserLike);
    }

    @Override
    public List<UserLike> findAllByProductId(Long productId) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("product_id").eq(productId))
                .orderBy(field("created_at").desc())
                .fetch()
                .map(this::toUserLike);
    }

    @Override
    public void deleteById(Long id) {
        dsl.deleteFrom(table(TABLE))
                .where(field("id").eq(id))
                .execute();
    }

    @Override
    public void deleteByUserIdAndProductId(Long userId, Long productId) {
        dsl.deleteFrom(table(TABLE))
                .where(field("user_id").eq(userId).and(field("product_id").eq(productId)))
                .execute();
    }

    private UserLike toUserLike(Record record) {
        return UserLike.builder()
                .id(record.get("id", Long.class))
                .userId(record.get("user_id", Long.class))
                .productId(record.get("product_id", Long.class))
                .createdAt(record.get("created_at", LocalDateTime.class))
                .build();
    }
}
