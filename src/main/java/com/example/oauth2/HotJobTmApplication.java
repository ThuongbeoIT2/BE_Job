package com.example.oauth2;

import com.example.oauth2.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class HotJobTmApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotJobTmApplication.class, args);
        System.out.println("hello");
    }

}
