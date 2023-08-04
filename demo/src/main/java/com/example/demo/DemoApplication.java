package com.example.demo;

import com.example.demo.config.DropWizardConfig;
import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.flyway.FlywayFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication extends Application<DropWizardConfig> {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Override
	public void initialize(Bootstrap<DropWizardConfig> bootstrap) {
		// ...
		bootstrap.addBundle(new FlywayBundle<DropWizardConfig>() {
			@Override
			public DataSourceFactory getDataSourceFactory(DropWizardConfig configuration) {
				return configuration.getDataSourceFactory();
			}

			@Override
			public FlywayFactory getFlywayFactory(DropWizardConfig configuration) {
				return configuration.getFlywayFactory();
			}
		});
	}

	@Override
	public void run(DropWizardConfig dropWizardConfig, Environment environment) throws Exception {

	}

}
