package com.taskmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String frontendEnvUrl = System.getenv("FRONTEND_URL");
        
        // Always allow these local dev URLs
        String[] allowedOrigins = {
            "http://localhost:5173",
            "http://localhost:3000",
            "http://localhost:8080",
            // Production Vercel frontend URLs
            "https://team-task-manager-seven-jade.vercel.app",
            "https://team-task-manager-hlks5lap8-vinay-kumar-reddys-projects.vercel.app",
            // Production Render backend URL (for self-reference)
            "https://teamtask-manager-6vlw.onrender.com"
        };
        
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
