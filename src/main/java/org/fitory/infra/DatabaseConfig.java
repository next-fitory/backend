package org.fitory.infra;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import core.ConfigurationAdapter;
import core.annotation.Component;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

@Component
public class DatabaseConfig {
    private final DSLContext dslContext;

    public DatabaseConfig() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(ConfigurationAdapter.getProperty("database.driverClassName"));
        config.setJdbcUrl(ConfigurationAdapter.getProperty("database.url"));
        config.setUsername(ConfigurationAdapter.getProperty("database.username"));
        config.setPassword(ConfigurationAdapter.getProperty("database.password"));

        this.dslContext = DSL.using(new HikariDataSource(config), SQLDialect.POSTGRES);
    }

    public DSLContext dsl() {
        return dslContext;
    }
}
