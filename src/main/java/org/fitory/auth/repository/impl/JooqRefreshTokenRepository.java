package org.fitory.auth.repository.impl;

import core.annotation.Repository;
import org.fitory.auth.domain.RefreshToken;
import org.fitory.auth.repository.RefreshTokenRepository;
import org.fitory.infra.DatabaseConfig;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Repository
public class JooqRefreshTokenRepository implements RefreshTokenRepository {
    private static final String TABLE = "refresh_tokens";
    private final DSLContext dsl;

    public JooqRefreshTokenRepository(DatabaseConfig databaseConfig) {
        this.dsl = databaseConfig.dsl();
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        Record record = dsl.insertInto(table(TABLE))
                .set(field("user_id"), refreshToken.getUserId())
                .set(field("token"), refreshToken.getToken())
                .set(field("expires_at"), refreshToken.getExpiresAt())
                .set(field("created_at"), refreshToken.getCreatedAt())
                .returning()
                .fetchOne();
        return toRefreshToken(record);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return dsl.select()
                .from(table(TABLE))
                .where(field("token").eq(token))
                .fetchOptional()
                .map(this::toRefreshToken);
    }

    @Override
    public void deleteByToken(String token) {
        dsl.deleteFrom(table(TABLE))
                .where(field("token").eq(token))
                .execute();
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        dsl.deleteFrom(table(TABLE))
                .where(field("user_id").eq(userId))
                .execute();
    }

    private RefreshToken toRefreshToken(Record record) {
        return RefreshToken.builder()
                .id(record.get("id", Long.class))
                .userId(record.get("user_id", Long.class))
                .token(record.get("token", String.class))
                .expiresAt(record.get("expires_at", LocalDateTime.class))
                .createdAt(record.get("created_at", LocalDateTime.class))
                .build();
    }
}
