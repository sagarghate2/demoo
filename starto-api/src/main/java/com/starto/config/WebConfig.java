package com.starto.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Support both plural and singular paths for maximum compatibility
        registry.addResourceHandler("/avatars/**", "/avatar/**")
                .addResourceLocations("classpath:/static/avatars/");
    }
}
