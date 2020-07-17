package org.technbolts.sandboxgateway.infra.db;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

@Configuration
public class Migration {

    private final DataSource migrationDataSource;

    public Migration(@Qualifier("db.migration.datasource") DataSource migrationDataSource) {
        this.migrationDataSource = migrationDataSource;
    }

    @PostConstruct
    public void migrate() {
        FluentConfiguration configuration = new FluentConfiguration()
                .dataSource(migrationDataSource)
                .locations("db/");
        Flyway flyway = new Flyway(configuration);
        flyway.migrate();
    }
}
