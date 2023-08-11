package com.example.demo.config;

import com.example.demo.jwt.BlackListService;
import com.example.demo.jwt.BlackListServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public BlackListService blackListService() {
        return new BlackListServiceImpl();
    }


}
