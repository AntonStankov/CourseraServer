package com.example.demo.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayFactory;

public class DropWizardConfig extends Configuration {
    @JsonProperty("database")
    private DataSourceFactory dataSourceFactory = new DataSourceFactory();

    @JsonProperty("flyway")
    private FlywayFactory flywayFactory = new FlywayFactory();

    public DataSourceFactory getDataSourceFactory() {
        return dataSourceFactory;
    }

    public FlywayFactory getFlywayFactory() {
        return flywayFactory;
    }
}
