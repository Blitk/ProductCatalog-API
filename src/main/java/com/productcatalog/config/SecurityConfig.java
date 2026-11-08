package com.productcatalog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Ativa a configuração de CORS definida abaixo
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // Desativa a proteção CSRF (comum para APIs REST)
            .csrf(csrf -> csrf.disable())
            // Define as regras de autorização de acessos
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll() // Libera publicamente os endpoints da API
                .requestMatchers("/uploads/**").permitAll() // Libera acesso às imagens enviadas
                .anyRequest().authenticated() // Qualquer outra rota exige autenticação
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8000")); // Origem do Front-end
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Aplica a todas as rotas
        return source;
    }
}
