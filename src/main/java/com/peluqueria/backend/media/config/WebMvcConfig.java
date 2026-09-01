package com.peluqueria.backend.media.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/media/**", "/api/media/files/**", "/uploads/**")
                .addResourceLocations("file:./uploads/media/", "file:uploads/media/", "file:./uploads/");
    }
}
