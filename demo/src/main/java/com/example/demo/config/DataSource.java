package com.example.demo.config;


import io.dropwizard.core.setup.Environment;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;

public class DataSource {

    Dotenv dotenv = new DotenvBuilder().load();
    @Autowired
    private Environment environment;

    String url = System.getenv("spring.datasource.url");
    String url1 = dotenv.get("spring.datasource.url");
    String username = System.getenv("spring.datasource.username");
    String username1 = dotenv.get("spring.datasource.username");

    String password = System.getenv("spring.datasource.password");
    String password1 = dotenv.get("spring.datasource.password");

    public java.sql.Connection createConnection() {
        try {
            java.sql.Connection connection = DriverManager.getConnection(url1,username1, password1);
            System.out.println("Connected to Database.");
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to database", e);
        }
    }
}
