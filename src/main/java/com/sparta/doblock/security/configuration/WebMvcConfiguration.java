package com.sparta.doblock.security.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry){
        corsRegistry.addMapping("/**")
                .allowedOrigins("http://localhost:3000",
                        "https://doblock.click",
                        "https://www.doblock.click",
                        "https://do-block.click",
                        "https://www.do-block.click")
                .allowedMethods("*")
                .exposedHeaders("Authorization", "RefreshToken")
                .allowCredentials(true);
    }
}
