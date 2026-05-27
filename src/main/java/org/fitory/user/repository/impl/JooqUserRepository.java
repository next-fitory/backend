package org.fitory.user.repository.impl;

import core.annotation.Repository;
import org.fitory.infra.DatabaseConfig;
import org.fitory.user.domain.User;
import org.fitory.user.repository.UserRepository;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class JooqUserRepository implements UserRepository {
    private static final String TABLE = "users";
    private final DSLContext dsl;

    public JooqUserRepository(DatabaseConfig databaseConfig) {
        this.dsl = databaseConfig.dsl();
    }

    @Override
    public User save(User user) {
        return user.getId() == null ? insert(user) : update(user);
    }

    private User insert(User user) {
        Record record = dsl.insertInto(table(TABLE))
                .set(field("email"), user.getEmail())
                .set(field("name"), user.getName())
                .set(field("created_at"), user.getCreatedAt())
                .set(field("updated_at"), user.getUpdatedAt())
                .set(field("deleted"), user.isDeleted())
                .returning()
                .fetchOne();
        return toUser(record);
    }

    private User update(User user) {
        Record record = dsl.update(table(TABLE))
                .set(field("email"), user.getEmail())
                .set(field("name"), user.getName())
                .set(field("updated_at"), user.getUpdatedAt())
                .where(field("id").eq(user.getId()))
                .returning()
                .fetchOne();
        return toUser(record);
    }

    @Override
    public Optional<User> findById(Long id) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("id").eq(id).and(field("deleted", Boolean.class).isFalse()))
                .fetchOptional()
                .map(this::toUser);
    }

    @Override
    public List<User> findAllByIds(List<Long> ids) {
        if (ids.isEmpty()) return List.of();
        return dsl.select()
                .from(table(TABLE))
                .where(field("id").in(ids).and(field("deleted", Boolean.class).isFalse()))
                .fetch()
                .map(this::toUser);
    }

    @Override
    public List<User> findAll() {
        return dsl.select()
                .from(table(TABLE))
                .where(field("deleted", Boolean.class).isFalse())
                .fetch()
                .map(this::toUser);
    }

    @Override
    public void deleteById(Long id) {
        dsl.update(table(TABLE))
                .set(field("deleted"), true)
                .set(field("updated_at"), LocalDateTime.now())
                .where(field("id").eq(id).and(field("deleted", Boolean.class).isFalse()))
                .execute();
    }

    private User toUser(Record record) {
        return User.builder()
                .id(record.get("id", Long.class))
                .email(record.get("email", String.class))
                .name(record.get("name", String.class))
                .createdAt(record.get("created_at", LocalDateTime.class))
                .updatedAt(record.get("updated_at", LocalDateTime.class))
                .deleted(record.get("deleted", Boolean.class))
                .build();
    }
}
