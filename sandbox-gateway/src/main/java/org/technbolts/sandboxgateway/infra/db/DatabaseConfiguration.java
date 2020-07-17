package org.technbolts.sandboxgateway.infra.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Configuration
public class DatabaseConfiguration {

    private final String dbHost;
    private final int dbPort;
    private final boolean dbSecure;
    private final String dbName;

    public DatabaseConfiguration(@Value("${db.host}") String dbHost,
                                 @Value("${db.port}") int dbPort,
                                 @Value("${db.secure}") boolean dbSecure,
                                 @Value("${db.name}") String dbName) {
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbSecure = dbSecure;
        this.dbName = dbName;
    }

    @Bean
    @Qualifier("db.poolConfigurer")
    public Consumer<HikariConfig> poolConfigurer(@Value("${db.pool.maximum-size}") int maxPoolSize,
                                                 @Value("${db.pool.minimum-idle}") int minIdle,
                                                 @Value("${db.pool.idle-timeout-ms}") long idleTimeOutMillis,
                                                 @Value("${db.pool.connection-timeout-ms}") long connectionTimeoutMillis,
                                                 @Value("${db.pool.maximum-lifetime-ms}") long maxConnectionLifetimeMillis) {
        return config -> {
            config.setMaximumPoolSize(maxPoolSize);
            config.setMinimumIdle(minIdle);
            config.setIdleTimeout(idleTimeOutMillis);
            config.setConnectionTimeout(connectionTimeoutMillis);
            config.setMaxLifetime(maxConnectionLifetimeMillis);
        };
    }

    @Bean(destroyMethod = "close")
    @Primary
    public HikariDataSource dataSource(@Value("${db.username}") String dbUsername,
                                       @Value("${db.password}") String dbPassword,
                                       @Qualifier("db.poolConfigurer") Consumer<HikariConfig> poolConfigurer) {
        return new HikariDataSourceBuilder(dbHost, dbPort, dbName, dbUsername, dbPassword, dbSecure)
                .applicationName("gateway")
                .configurer(poolConfigurer)
                .createDataSource();
    }

    @Bean(destroyMethod = "close")
    @Qualifier("db.migration.datasource")
    public HikariDataSource migrationDataSource(@Value("${db.migration.username}") String dbUsername,
                                                @Value("${db.migration.password}") String dbPassword,
                                                @Value("${db.migration.owner}") String dbMigrationOwner,
                                                @Qualifier("db.poolConfigurer") Consumer<HikariConfig> poolConfigurer) {
        return new HikariDataSourceBuilder(dbHost, dbPort, dbName, dbUsername, dbPassword, dbSecure)
                .applicationName("gateway")
                .configurer(poolConfigurer)
                .configurer(config -> {
                    config.setMaximumPoolSize(2);
                    config.setMinimumIdle(0);
                    config.setIdleTimeout(TimeUnit.MINUTES.toMillis(1));
                    config.setConnectionInitSql("SET ROLE " + dbMigrationOwner);
                }).createDataSource();
    }
}
