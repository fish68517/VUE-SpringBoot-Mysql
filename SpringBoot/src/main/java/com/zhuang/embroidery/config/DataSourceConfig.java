package com.zhuang.embroidery.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 数据源配置类
 * 配置数据库连接池和数据源
 */
@Configuration
public class DataSourceConfig {

    /**
     * 配置数据源
     * 使用 HikariCP 连接池（SpringBoot 默认）
     *
     * @return 配置好的数据源
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

}
