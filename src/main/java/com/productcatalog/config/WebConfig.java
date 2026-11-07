package com.productcatalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadDir;

    public WebConfig(
            PageableHandlerMethodArgumentResolverCustomizer customizer,
            @Value("${app.upload.dir:uploads/products}") String uploadDir) {

        this.uploadDir = uploadDir;

        // Limita a paginação a 50 registros por requisição.
        customizer.customize(resolver -> {
            resolver.setMaxPageSize(50);
        });
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        Path uploadPath = Path.of(uploadDir)
                .toAbsolutePath()
                .normalize();

        registry.addResourceHandler("/uploads/products/**")
                .addResourceLocations(uploadPath.toUri().toString());
    }
}
