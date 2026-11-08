package com.productcatalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadDir;

    // O construtor correto NÃO deve receber o PageableHandlerMethodArgumentResolverCustomizer
    public WebConfig(@Value("${app.upload.dir:uploads/products}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer customizePagination() {
        return resolver -> resolver.setMaxPageSize(50);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Path.of(uploadDir).toAbsolutePath().normalize();
        registry.addResourceHandler("/uploads/products/**")
                .addResourceLocations(uploadPath.toUri().toString());
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Aplica o CORS a todas as rotas que começam com /api
                .allowedOrigins("http://localhost:8000") // Permite requisições vindas do seu front-end
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") // Métodos HTTP permitidos
                .allowedHeaders("*") // Permite todos os cabeçalhos (headers)
                .allowCredentials(true); // Permite o envio de cookies/autenticação se necessário
    }
}
