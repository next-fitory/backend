package org.fitory.review.repository.impl;

import org.springframework.stereotype.Repository;
import org.fitory.infra.DatabaseConfig;
import org.fitory.review.domain.Review;
import org.fitory.review.repository.ReviewRepository;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class JooqReviewRepository implements ReviewRepository {
    private static final String TABLE = "reviews";
    private final DSLContext dsl;

    public JooqReviewRepository(DatabaseConfig databaseConfig) {
        this.dsl = databaseConfig.dsl();
    }

    @Override
    public Review save(Review review) {
        return review.getId() == null ? insert(review) : update(review);
    }

    private Review insert(Review review) {
        Long id = dsl.insertInto(table(TABLE))
                .set(field("product_id"), review.getProductId())
                .set(field("user_id"), review.getUserId())
                .set(field("content"), review.getContent())
                .set(field("rating"), review.getRating())
                .set(field("created_at"), review.getCreatedAt())
                .set(field("updated_at"), review.getUpdatedAt())
                .set(field("deleted"), review.isDeleted())
                .returning(field("id", Long.class))
                .fetchOne(field("id", Long.class));
        return review.toBuilder().id(id).build();
    }

    private Review update(Review review) {
        dsl.update(table(TABLE))
                .set(field("content"), review.getContent())
                .set(field("rating"), review.getRating())
                .set(field("updated_at"), review.getUpdatedAt())
                .where(field("id").eq(review.getId()))
                .execute();
        return review;
    }

    @Override
    public Optional<Review> findById(Long id) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("id").eq(id).and(field("deleted", Boolean.class).isFalse()))
                .fetchOptional()
                .map(this::toReview);
    }

    @Override
    public List<Review> findAll(int page, int size) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("deleted", Boolean.class).isFalse())
                .orderBy(field("created_at").desc())
                .limit(size)
                .offset((long) page * size)
                .fetch()
                .map(this::toReview);
    }

    @Override
    public long count() {
        return dsl.selectCount()
                .from(table(TABLE))
                .where(field("deleted", Boolean.class).isFalse())
                .fetchOne(0, Long.class);
    }

    @Override
    public List<Review> findAllByProductId(Long productId, int page, int size) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("product_id").eq(productId).and(field("deleted", Boolean.class).isFalse()))
                .orderBy(field("created_at").desc())
                .limit(size)
                .offset((long) page * size)
                .fetch()
                .map(this::toReview);
    }

    @Override
    public long countByProductId(Long productId) {
        return dsl.selectCount()
                .from(table(TABLE))
                .where(field("product_id").eq(productId).and(field("deleted", Boolean.class).isFalse()))
                .fetchOne(0, Long.class);
    }

    @Override
    public void deleteById(Long id) {
        dsl.update(table(TABLE))
                .set(field("deleted"), true)
                .set(field("updated_at"), LocalDateTime.now())
                .where(field("id").eq(id).and(field("deleted", Boolean.class).isFalse()))
                .execute();
    }

    private Review toReview(Record record) {
        return Review.builder()
                .id(record.get("id", Long.class))
                .productId(record.get("product_id", Long.class))
                .userId(record.get("user_id", Long.class))
                .content(record.get("content", String.class))
                .rating(record.get("rating", Integer.class))
                .createdAt(record.get("created_at", LocalDateTime.class))
                .updatedAt(record.get("updated_at", LocalDateTime.class))
                .deleted(record.get("deleted", Boolean.class))
                .build();
    }
}
