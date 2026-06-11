package org.fitory.brand.repository.impl;

import org.fitory.brand.domain.Brand;
import org.fitory.brand.repository.BrandRepository;
import org.fitory.infra.DatabaseConfig;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class JooqBrandRepository implements BrandRepository {
    private static final String TABLE = "brands";
    private final DSLContext dsl;

    public JooqBrandRepository(DatabaseConfig databaseConfig) {
        this.dsl = databaseConfig.dsl();
    }

    @Override
    public Brand save(Brand brand) {
        if (brand.getId() == null) {
            return insert(brand);
        }
        return update(brand);
    }

    private Brand insert(Brand brand) {
        Record record = dsl.insertInto(table(TABLE))
                .set(field("name"), brand.getName())
                .set(field("image_url"), brand.getImageUrl())
                .set(field("created_at"), brand.getCreatedAt())
                .set(field("updated_at"), brand.getUpdatedAt())
                .set(field("deleted"), brand.isDeleted())
                .returning()
                .fetchOne();

        return toBrand(record);
    }

    private Brand update(Brand brand) {
        Record record = dsl.update(table(TABLE))
                .set(field("name"), brand.getName())
                .set(field("image_url"), brand.getImageUrl())
                .set(field("updated_at"), brand.getUpdatedAt())
                .where(field("id").eq(brand.getId()))
                .returning()
                .fetchOne();

        return toBrand(record);
    }

    @Override
    public Optional<Brand> findById(Long id) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("id").eq(id).and(field("deleted", Boolean.class).isFalse()))
                .fetchOptional()
                .map(this::toBrand);
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
    public List<Brand> findAll() {
        return dsl.select()
                .from(table(TABLE))
                .where(field("deleted", Boolean.class).isFalse())
                .fetch()
                .map(this::toBrand);
    }

    @Override
    public Optional<Brand> findByName(String name) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("name").eq(name).and(field("deleted", Boolean.class).isFalse()))
                .fetchOptional()
                .map(this::toBrand);
    }

    private Brand toBrand(Record record) {
        return Brand.builder()
                .id(record.get("id", Long.class))
                .name(record.get("name", String.class))
                .imageUrl(record.get("image_url", String.class))
                .createdAt(record.get("created_at", LocalDateTime.class))
                .updatedAt(record.get("updated_at", LocalDateTime.class))
                .deleted(record.get("deleted", Boolean.class))
                .build();
    }
}
