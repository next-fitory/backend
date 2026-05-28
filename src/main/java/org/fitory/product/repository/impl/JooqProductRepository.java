package org.fitory.product.repository.impl;

import core.annotation.Repository;
import org.fitory.infra.DatabaseConfig;
import org.fitory.product.domain.Product;
import org.fitory.product.repository.ProductRepository;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class JooqProductRepository implements ProductRepository {
    private static final String TABLE = "products";
    private final DSLContext dsl;

    public JooqProductRepository(DatabaseConfig databaseConfig) {
        this.dsl = databaseConfig.dsl();
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            return insert(product);
        }
        return update(product);
    }

    private Product insert(Product product) {
        Record record = dsl.insertInto(table(TABLE))
                .set(field("brand_id"), product.getBrandId())
                .set(field("category_id"), product.getCategoryId())
                .set(field("name"), product.getName())
                .set(field("description"), product.getDescription())
                .set(field("price"), product.getPrice())
                .set(field("discount_rate"), product.getDiscountRate())
                .set(field("stock"), product.getStock())
                .set(field("image_url"), product.getImageUrl())
                .set(field("deleted"), product.isDeleted())
                .set(field("created_at"), product.getCreatedAt())
                .set(field("updated_at"), product.getUpdatedAt())
                .returning()
                .fetchOne();

        return toProduct(record);
    }

    private Product update(Product product) {
        Record record = dsl.update(table(TABLE))
                .set(field("brand_id"), product.getBrandId())
                .set(field("category_id"), product.getCategoryId())
                .set(field("name"), product.getName())
                .set(field("description"), product.getDescription())
                .set(field("price"), product.getPrice())
                .set(field("discount_rate"), product.getDiscountRate())
                .set(field("stock"), product.getStock())
                .set(field("image_url"), product.getImageUrl())
                .set(field("deleted"), product.isDeleted())
                .set(field("updated_at"), product.getUpdatedAt())
                .where(field("id").eq(product.getId()))
                .returning()
                .fetchOne();

        return toProduct(record);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("id").eq(id).and(field("deleted", Boolean.class).isFalse()))
                .fetchOptional()
                .map(this::toProduct);
    }

    @Override
    public List<Product> findAll() {
        return dsl.select()
                .from(table(TABLE))
                .where(field("deleted", Boolean.class).isFalse())
                .fetch()
                .map(this::toProduct);
    }

    @Override
    public List<Product> findAllByCategoryId(Long categoryId) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("category_id").eq(categoryId)
                        .and(field("deleted", Boolean.class).isFalse()))
                .fetch()
                .map(this::toProduct);
    }

    @Override
    public List<Product> findAllByOrderByCreatedAtDesc() {
        return dsl.select()
                .from(table(TABLE))
                .where(field("deleted", Boolean.class).isFalse())
                .orderBy(field("created_at").desc())
                .fetch()
                .map(this::toProduct);
    }

    @Override
    public List<Product> findAllByBrandId(Long brandId, int page, int size) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("brand_id").eq(brandId)
                        .and(field("deleted", Boolean.class).isFalse()))
                .orderBy(field("created_at").desc())
                .limit(size)
                .offset((long) page * size)
                .fetch()
                .map(this::toProduct);
    }

    @Override
    public long countByBrandId(Long brandId) {
        return dsl.selectCount()
                .from(table(TABLE))
                .where(field("brand_id").eq(brandId)
                        .and(field("deleted", Boolean.class).isFalse()))
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

    private Product toProduct(Record r) {
        return Product.builder()
                .id(r.get("id", Long.class))
                .brandId(r.get("brand_id", Long.class))
                .categoryId(r.get("category_id", Long.class))
                .name(r.get("name", String.class))
                .description(r.get("description", String.class))
                .price(r.get("price", Integer.class))
                .discountRate(r.get("discount_rate", Integer.class))
                .stock(r.get("stock", Integer.class))
                .imageUrl(r.get("image_url", String.class))
                .deleted(r.get("deleted", Boolean.class))
                .createdAt(r.get("created_at", LocalDateTime.class))
                .updatedAt(r.get("updated_at", LocalDateTime.class))
                .build();
    }
}
