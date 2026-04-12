package com.travelMemory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration for Travel Memory System
 * Provides API documentation and interactive API testing interface
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Travel Memory System API")
                        .version("1.0.0")
                        .description("Comprehensive API documentation for the Travel Memory System. " +
                                "This system provides features for travel planning, memory management, " +
                                "multimedia recording, map footprints, and social interactions.")
                        .contact(new Contact()
                                .name("Travel Memory System Team")
                                .email("support@travelmemory.com")
                                .url("https://travelmemory.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"));
    }
}
