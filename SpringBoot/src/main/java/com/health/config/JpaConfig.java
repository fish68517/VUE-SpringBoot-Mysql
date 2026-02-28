package com.health.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA配置类
 * 启用JPA仓储扫描和自动配置
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.health.repository")
public class JpaConfig {
}
