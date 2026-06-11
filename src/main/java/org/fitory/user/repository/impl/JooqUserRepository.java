package org.fitory.user.repository.impl;

import org.springframework.stereotype.Repository;
import org.fitory.auth.domain.Role;
import org.fitory.auth.domain.User;
import org.fitory.infra.DatabaseConfig;
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
    public Optional<User> findByEmail(String email) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("email").eq(email))
                .fetchOptional()
                .map(this::toUser);
    }

    @Override
    public Optional<User> findById(Long id) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("id").eq(id))
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

    private User toUser(Record record) {
        return User.builder()
                .id(record.get("id", Long.class))
                .email(record.get("email", String.class))
                .name(record.get("name", String.class))
                .password(record.get("password", String.class))
                .role(record.get("role", Role.class))
                .createdAt(record.get("created_at", LocalDateTime.class))
                .build();
    }
}
