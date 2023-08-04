package com.example.demo.config;


import io.dropwizard.core.setup.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {

    @Autowired
    private Environment environment;

    String url = System.getenv("spring.datasource.url");
    String username = System.getenv("spring.datasource.username");
    String password = System.getenv("spring.datasource.password");

    public java.sql.Connection createConnection() {
        try {
            java.sql.Connection connection = DriverManager.getConnection(url,username, password);
            System.out.println("Connected to Database.");
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to database", e);
        }
    }
}
