package com.powerinspection.config;

import com.powerinspection.security.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AppProperties appProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .excludePathPatterns("/api/auth/login", "/api/auth/register", "/images/**", "/error");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path imagePath = Paths.get(appProperties.getImageDir()).toAbsolutePath().normalize();
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + imagePath.toString() + "/");
    }
}
