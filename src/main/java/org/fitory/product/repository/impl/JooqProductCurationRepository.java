package org.fitory.product.repository.impl;

import org.springframework.stereotype.Repository;
import org.fitory.infra.DatabaseConfig;
import org.fitory.product.dto.ProductResponse;
import org.fitory.product.repository.ProductCurationRepository;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class JooqProductCurationRepository implements ProductCurationRepository {
    private final DSLContext dsl;

    public JooqProductCurationRepository(DatabaseConfig databaseConfig) {
        this.dsl = databaseConfig.dsl();
    }

    @Override
    public List<ProductResponse> findNewArrivals() {
        return dsl.select(
                        field("p.id").as("p_id"),
                        field("p.name").as("name"),
                        field("p.price").as("price"),
                        field("p.discount_rate").as("discount_rate"),
                        field("p.stock").as("stock"),
                        field("p.image_url").as("image_url"),
                        field("b.name").as("brand_name")
                )
                .from(table("new_arrivals").as("na"))
                .join(table("products").as("p")).on(field("na.product_id").eq(field("p.id")))
                .join(table("brands").as("b")).on(field("p.brand_id").eq(field("b.id")))
                .where(field("na.deleted", Boolean.class).isFalse()
                        .and(field("p.deleted", Boolean.class).isFalse()))
                .orderBy(field("na.created_at").desc())
                .fetch()
                .map(this::toProductResponse);
    }

    @Override
    public List<ProductResponse> findRanked() {
        return dsl.select(
                        field("p.id").as("p_id"),
                        field("p.name").as("name"),
                        field("p.price").as("price"),
                        field("p.discount_rate").as("discount_rate"),
                        field("p.stock").as("stock"),
                        field("p.image_url").as("image_url"),
                        field("b.name").as("brand_name")
                )
                .from(table("ranked_products").as("rp"))
                .join(table("products").as("p")).on(field("rp.product_id").eq(field("p.id")))
                .join(table("brands").as("b")).on(field("p.brand_id").eq(field("b.id")))
                .where(field("rp.deleted", Boolean.class).isFalse()
                        .and(field("p.deleted", Boolean.class).isFalse()))
                .orderBy(field("rp.created_at").desc())
                .fetch()
                .map(this::toProductResponse);
    }

    private ProductResponse toProductResponse(Record r) {
        int price = r.get("price", Integer.class);
        int discountRate = r.get("discount_rate", Integer.class);
        int salePrice = (int) (price * (100 - discountRate) * 0.01);
        return ProductResponse.builder()
                .id(r.get("p_id", Long.class))
                .name(r.get("name", String.class))
                .price(price)
                .salePrice(salePrice)
                .discountRate(discountRate)
                .imageUrl(r.get("image_url", String.class))
                .brandName(r.get("brand_name", String.class))
                .stock(r.get("stock", Integer.class))
                .build();
    }
}
