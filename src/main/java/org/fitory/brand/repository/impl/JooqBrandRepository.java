package org.fitory.brand.repository.impl;

import core.annotation.Repository;
import org.fitory.brand.domain.Brand;
import org.fitory.brand.repository.BrandRepository;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class JooqBrandRepository implements BrandRepository {
    private static final String TABLE = "brands";
    private final DSLContext dsl;

    public JooqBrandRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public void save(Brand brand) {

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

    @Override
    public Optional<Brand> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public List<Brand> findAll() {
        return List.of();
    }

    @Override
    public Optional<Brand> findByName(String name) {
        return Optional.empty();
    }


    private Brand toBrand(Record record) {
        return Brand.builder().build();
    }
}
