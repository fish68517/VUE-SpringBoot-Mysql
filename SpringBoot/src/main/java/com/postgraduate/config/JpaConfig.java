package com.postgraduate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * JPA configuration for entity scanning and auditing.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.postgraduate.repository")
@EnableJpaAuditing
public class JpaConfig {
}
