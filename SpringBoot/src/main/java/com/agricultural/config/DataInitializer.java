package com.agricultural.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 数据初始化配置类
 * 在应用启动时执行SQL脚本初始化数据库
 */
@Slf4j
@Configuration
public class DataInitializer {
    
    /**
     * 初始化数据库脚本
     * 仅在非测试环境下执行
     */
    @Bean
    @Profile("!test")
    public CommandLineRunner initializeDatabase(DataSource dataSource) {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                log.info("开始初始化数据库...");
                ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/init.sql"));
                log.info("数据库初始化完成");
            } catch (Exception e) {
                log.error("数据库初始化失败", e);
                throw new RuntimeException("数据库初始化失败", e);
            }
        };
    }
}
