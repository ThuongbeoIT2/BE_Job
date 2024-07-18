package com.example.oauth2.config;


import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConfigCloudinary {
    private Cloudinary cloudinary;
    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", " dqvr7kat6");
        config.put("api_key", "712758645674169");
        config.put("api_secret", "CN0CG-NGsuJIpwn2UdLCBvX72FE");
        config.put("secure", "true");

        return new Cloudinary(config);
    }

}