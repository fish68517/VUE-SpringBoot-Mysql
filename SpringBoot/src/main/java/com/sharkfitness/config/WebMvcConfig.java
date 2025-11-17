package com.sharkfitness.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration to register interceptors
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")  // Apply to all API endpoints
                .excludePathPatterns(
                        "/api/auth/register",    // Allow registration
                        "/api/auth/login",       // Allow login
                        "/api/search",           // Allow public search
                        "/api/resources",        // Allow public resource browsing
                        "/api/resources/**",     // Allow public resource details
                        "/api/dynamics",         // Allow public community feed
                        "/api/dynamics/**",      // Allow public post details
                        "/api/coaches",          // Allow public coach listing
                        "/api/coaches/**"        // Allow public coach profiles
                );
    }
}
