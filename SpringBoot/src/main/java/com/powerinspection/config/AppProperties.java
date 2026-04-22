package com.powerinspection.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String imageDir;
    private String baseUrl;
    private String jwtSecret;
    private long jwtExpireHours;
}
