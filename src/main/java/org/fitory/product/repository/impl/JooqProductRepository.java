package org.fitory.product.repository.impl;

import core.annotation.Repository;
import org.fitory.infra.DatabaseConfig;
import org.fitory.product.domain.Product;
import org.fitory.product.dto.ProductResponse;
import org.fitory.product.dto.ProductSearchRequest;
import org.fitory.product.repository.ProductRepository;
import org.jooq.Condition;
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
        return dsl.select(productWithBrandFields())
                .from(table("products").as("p"))
                .leftJoin(table("brands").as("b")).on(field("p.brand_id").eq(field("b.id")))
                .where(field("p.id").eq(id).and(field("p.deleted", Boolean.class).isFalse()))
                .fetchOptional()
                .map(this::toProductWithBrand);
    }

    @Override
    public List<Product> findAll() {
        return dsl.select(productWithBrandFields())
                .from(table("products").as("p"))
                .leftJoin(table("brands").as("b")).on(field("p.brand_id").eq(field("b.id")))
                .where(field("p.deleted", Boolean.class).isFalse())
                .fetch()
                .map(this::toProductWithBrand);
    }

    @Override
    public List<Product> findAllByCategoryId(Long categoryId, int page, int size) {
        return dsl.select(productWithBrandFields())
                .from(table("products").as("p"))
                .leftJoin(table("categories").as("b")).on(field("p.brand_id").eq(field("b.id")))
                .where(field("p.category_id").eq(categoryId)
                        .and(field("p.deleted", Boolean.class).isFalse()))
                .orderBy(field("p.created_at").desc())
                .limit(size)
                .offset((long) page * size)
                .fetch()
                .map(this::toProductWithBrand);
    }

    @Override
    public List<Product> findAllByOrderByCreatedAtDesc() {
        return dsl.select(productWithBrandFields())
                .from(table("products").as("p"))
                .leftJoin(table("brands").as("b")).on(field("p.brand_id").eq(field("b.id")))
                .where(field("p.deleted", Boolean.class).isFalse())
                .orderBy(field("p.created_at").desc())
                .fetch()
                .map(this::toProductWithBrand);
    }

    @Override
    public List<Product> findAllByBrandId(Long brandId, int page, int size) {
        return dsl.select(productWithBrandFields())
                .from(table("products").as("p"))
                .leftJoin(table("brands").as("b")).on(field("p.brand_id").eq(field("b.id")))
                .where(field("p.brand_id").eq(brandId)
                        .and(field("p.deleted", Boolean.class).isFalse()))
                .orderBy(field("p.created_at").desc())
                .limit(size)
                .offset((long) page * size)
                .fetch()
                .map(this::toProductWithBrand);
    }

    @Override
    public long countByBrandId(Long brandId) {
        return dsl.selectCount()
                .from(table(TABLE))
                .where(field("brand_id").eq(brandId)
                        .and(field("deleted", Boolean.class).isFalse()))
                .fetchOne(0, Long.class);
    }

    private org.jooq.SelectFieldOrAsterisk[] productWithBrandFields() {
        return new org.jooq.SelectFieldOrAsterisk[]{
                field("p.id"),
                field("p.brand_id"),
                field("p.category_id"),
                field("p.name").as("product_name"),
                field("p.description"),
                field("p.price"),
                field("p.discount_rate"),
                field("p.stock"),
                field("p.image_url"),
                field("p.deleted"),
                field("p.created_at"),
                field("p.updated_at"),
                field("b.name").as("brand_name")
        };
    }

    private Product toProductWithBrand(Record r) {
        return Product.builder()
                .id(r.get("id", Long.class))
                .brandId(r.get("brand_id", Long.class))
                .categoryId(r.get("category_id", Long.class))
                .name(r.get("product_name", String.class))
                .description(r.get("description", String.class))
                .price(r.get("price", Integer.class))
                .discountRate(r.get("discount_rate", Integer.class))
                .stock(r.get("stock", Integer.class))
                .imageUrl(r.get("image_url", String.class))
                .brandName(r.get("brand_name", String.class))
                .deleted(r.get("deleted", Boolean.class))
                .createdAt(r.get("created_at", LocalDateTime.class))
                .updatedAt(r.get("updated_at", LocalDateTime.class))
                .build();
    }

    @Override
    public List<ProductResponse> search(ProductSearchRequest request, int page, int size) {
        Condition condition = searchCondition(request);
        return dsl.select(
                        field("p.id").as("p_id"),
                        field("p.name").as("name"),
                        field("p.price").as("price"),
                        field("p.discount_rate").as("discount_rate"),
                        field("p.stock").as("stock"),
                        field("p.image_url").as("image_url"),
                        field("b.name").as("brand_name")
                )
                .from(table("products").as("p"))
                .join(table("brands").as("b")).on(field("p.brand_id").eq(field("b.id")))
                .join(table("categories").as("c")).on(field("p.category_id").eq(field("c.id")))
                .where(condition)
                .orderBy(field("p.created_at").desc())
                .limit(size)
                .offset((long) page * size)
                .fetch()
                .map(this::toProductResponse);
    }

    @Override
    public long countSearch(ProductSearchRequest request) {
        Condition condition = searchCondition(request);
        return dsl.selectCount()
                .from(table("products").as("p"))
                .join(table("brands").as("b")).on(field("p.brand_id").eq(field("b.id")))
                .join(table("categories").as("c")).on(field("p.category_id").eq(field("c.id")))
                .where(condition)
                .fetchOne(0, Long.class);
    }

    private Condition searchCondition(ProductSearchRequest req) {
        Condition condition = field("p.deleted", Boolean.class).isFalse();
        if (req.categorySlug() != null && !req.categorySlug().isBlank()) {
            condition = condition.and(field("c.slug").eq(req.categorySlug()));
        }
        if (req.brandName() != null && !req.brandName().isBlank()) {
            condition = condition.and(field("b.name").equalIgnoreCase(req.brandName()));
        }
        if (req.keyword() != null && !req.keyword().isBlank()) {
            condition = condition.and(field("p.name").likeIgnoreCase("%" + req.keyword() + "%"));
        }
        return condition;
    }

    private ProductResponse toProductResponse(Record r) {
        int price = r.get("price", Integer.class);
        int discountRate = r.get("discount_rate", Integer.class);
        return ProductResponse.builder()
                .id(r.get("p_id", Long.class))
                .name(r.get("name", String.class))
                .price(price)
                .salePrice((int) (price * (100 - discountRate) * 0.01))
                .discountRate(discountRate)
                .stock(r.get("stock", Integer.class))
                .imageUrl(r.get("image_url", String.class))
                .brandName(r.get("brand_name", String.class))
                .build();
    }

    @Override
    public void deleteById(Long id) {
        dsl.update(table(TABLE))
                .set(field("deleted"), true)
                .set(field("updated_at"), LocalDateTime.now())
                .where(field("id").eq(id).and(field("deleted", Boolean.class).isFalse()))
                .execute();
    }

    @Override
    public long countByCategoryId(Long categoryId) {
        return dsl.selectCount()
                .from(table(TABLE))
                .where(field("category_id").eq(categoryId)
                        .and(field("deleted", Boolean.class).isFalse()))
                .fetchOne(0, Long.class);
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
