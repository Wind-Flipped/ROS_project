package com.se.hw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = "D:\\A_SE\\team04-project\\backend\\src\\main\\resources\\static\\";
        registry.addResourceHandler("/**").addResourceLocations("file:" + path);
    }
}