package com.zhuang.embroidery.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * 数据库初始化器
 * 在应用启动时执行数据库初始化脚本
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    /**
     * 应用启动时执行数据库初始化
     *
     * @param args 命令行参数
     * @throws Exception 如果初始化失败
     */
    @Override
    public void run(String... args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            // 执行数据库初始化脚本
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("db/init.sql"));
            System.out.println("✓ 数据库初始化成功");
        } catch (Exception e) {
            System.err.println("✗ 数据库初始化失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
