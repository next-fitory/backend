package org.fitory.category.repository.impl;

import core.annotation.Repository;
import org.fitory.category.domain.Category;
import org.fitory.category.repository.CategoryRepository;
import org.fitory.infra.DatabaseConfig;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.util.List;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class JooqCategoryRepository implements CategoryRepository {
    private static final String TABLE = "categories";
    private final DSLContext dsl;

    public JooqCategoryRepository(DatabaseConfig databaseConfig) {
        this.dsl = databaseConfig.dsl();
    }

    @Override
    public List<Category> findAll() {
        return dsl.select()
                .from(table(TABLE))
                .orderBy(field("sort_order"))
                .fetch()
                .map(this::toCategory);
    }

    private Category toCategory(Record record) {
        return Category.builder()
                .id(record.get("id", Long.class))
                .label(record.get("label", String.class))
                .emoji(record.get("emoji", String.class))
                .sortOrder(record.get("sort_order", Long.class))
                .slug(record.get("slug", String.class))
                .build();
    }
}
