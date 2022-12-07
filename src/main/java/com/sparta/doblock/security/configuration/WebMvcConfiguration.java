package com.sparta.doblock.security.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        corsRegistry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080", "http://3.37.87.20:8080")
                .allowedMethods("*")
                .exposedHeaders("Authorization", "RefreshToken")
                .allowCredentials(true);
    }
}
