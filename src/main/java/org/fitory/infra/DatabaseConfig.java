package org.fitory.infra;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    private final HikariDataSource dataSource;
    private final DSLContext dslContext;

    public DatabaseConfig(
            @Value("${database.driverClassName}") String driverClassName,
            @Value("${database.url}") String jdbcUrl,
            @Value("${database.username}") String username,
            @Value("${database.password}") String password) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        this.dataSource = new HikariDataSource(config);
        this.dslContext = DSL.using(dataSource, SQLDialect.POSTGRES);
    }

    @Bean
    public DataSource dataSource() {
        return dataSource;
    }

    public DSLContext dsl() {
        return dslContext;
    }
}
