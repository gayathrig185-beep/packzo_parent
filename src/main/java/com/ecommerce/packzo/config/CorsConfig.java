package com.ecommerce.packzo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("https://zm46c645-8081.inc1.devtunnels.ms","http://localhost:3000","https://j8jbjw1q-3000.inc1.devtunnels.ms","http://localhost:54768") // Angular URL
                        .allowedMethods("*")
                        .allowedHeaders("*");
            }
        };
    }
}