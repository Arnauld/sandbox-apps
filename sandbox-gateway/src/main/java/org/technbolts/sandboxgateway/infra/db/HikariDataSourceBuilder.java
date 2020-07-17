package org.technbolts.sandboxgateway.infra.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class HikariDataSourceBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(HikariDataSourceBuilder.class);
    private final String host;
    private final int port;
    private final String dbName;
    private final String username;
    private final String password;
    private final boolean secure;
    //
    private String applicationName;
    private Consumer<HikariConfig> configurer;

    public HikariDataSourceBuilder(String host,
                                   int port,
                                   String dbName,
                                   String username,
                                   String password,
                                   boolean secure) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
        this.secure = secure;
        this.configurer = this::defaultConfiguration;
    }

    protected void defaultConfiguration(HikariConfig config) {
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("auto-commit", true);
    }

    public HikariDataSourceBuilder applicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public HikariDataSourceBuilder configurer(Consumer<HikariConfig> configurer) {
        this.configurer = configurer.andThen(configurer);
        return this;
    }

    public HikariDataSource createDataSource() {
        String jdbcUrl = buildPostgresUrl(host, port, dbName, applicationName, secure);
        String obfuscatedPassword = password.replaceAll(".", "*");

        LOGGER.info("JDBC Url : {}, username: {}, password: {}, secure: {}", jdbcUrl, username, obfuscatedPassword, secure);

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        configurer.accept(config);
        return new HikariDataSource(config);
    }

    private static String buildPostgresUrl(String host, int port, String dbName, String applicationName, boolean secure) {
        return "jdbc:postgresql://" + host + ":" + port + "/" + dbName + "?ApplicationName=" + applicationName + "&ssl=" + secure;
    }
}
