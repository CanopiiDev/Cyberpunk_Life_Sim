package com.cyberpunklifesim.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS: de browser blokkeert standaard verzoeken van de ene website (de React app op poort 5173)
 * naar een andere (deze API op poort 8080). Hier zeggen we dat de React app dat wel mag.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://localhost:3000")
                .allowedMethods("GET", "POST", "DELETE");
    }
}
