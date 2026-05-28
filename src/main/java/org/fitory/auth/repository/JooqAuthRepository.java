package org.fitory.auth.repository;

import core.annotation.Repository;
import org.fitory.auth.domain.Role;
import org.fitory.auth.domain.User;
import org.fitory.infra.DatabaseConfig;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class JooqAuthRepository implements UserRepository{
    private static final String TABLE = "users";
    private final DSLContext dsl;

    public JooqAuthRepository(DatabaseConfig databaseConfig) {
        this.dsl = databaseConfig.dsl();
    }

    @Override
    public User save(User user) {
        return user.getId() == null ? insert(user) : null;
    }

    private User insert(User user) {
        Record record = dsl.insertInto(table(TABLE))
                .set(field("email"), user.getEmail())
                .set(field("password"), user.getPassword())
                .set(field("name"), user.getName())
                .set(field("role"), field("?::role", String.class, user.getRole().name()))
                .returning()
                .fetchOne();
        return user;
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
