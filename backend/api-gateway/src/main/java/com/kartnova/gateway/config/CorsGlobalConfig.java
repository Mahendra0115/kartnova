package com.kartnova.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsGlobalConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        // CORS rules ko define karne ke liye config object
        CorsConfiguration corsConfig = new CorsConfiguration();

        // IMPORTANT:
        // Local frontend dev servers ko allow kar rahe hain
        // Future me production frontend domain yahin add hoga
        corsConfig.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173"
        ));

        // IMPORTANT:
        // Auth headers / cookies future compatibility ke liye credentials allow
        corsConfig.setAllowCredentials(true);

        // Common methods allow
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "PATCH",
                "OPTIONS"
        ));

        // Dev phase me all headers allow
        corsConfig.setAllowedHeaders(List.of("*"));

        // Dev phase me all response headers expose
        corsConfig.setExposedHeaders(List.of("*"));

        // Browser preflight cache time
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // IMPORTANT:
        // Ye config gateway ke sabhi routes par apply hogi
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}