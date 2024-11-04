package com.example.oauth2.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Configuration
public class AppConfig {

    @Bean
    public BlockingQueue<Long> orderQueue() {
        return new LinkedBlockingQueue<>();
    }
}