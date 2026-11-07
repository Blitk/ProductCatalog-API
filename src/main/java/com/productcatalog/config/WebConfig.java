package com.productcatalog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class WebConfig {

    public WebConfig(
            PageableHandlerMethodArgumentResolverCustomizer customizer) {

        customizer.customize(resolver -> {
            resolver.setMaxPageSize(50);
        });
    }
}
